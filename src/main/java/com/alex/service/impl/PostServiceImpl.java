package com.alex.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import com.alex.common.Constant;
import com.alex.entity.Comment;
import com.alex.entity.Post;
import com.alex.entity.User;
import com.alex.entity.UserMessage;
import com.alex.mapper.PostMapper;
import com.alex.service.*;
import com.alex.util.RedisUtil;
import com.alex.vo.PostVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author alex
 * @since 2020-06-15
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserCollectionService userCollectionService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserMessageService userMessageService;

    @Autowired
    private UserService userService;

    @Autowired
    private WebSocketService webSocketService;

    @Override
    public IPage<PostVO> getPostByPage(Page page, Long categoryId, Long userId, Integer level, Boolean recommend, String order) {
        if(level == null){
            level = -1;
        }
        //构造搜索条件
        QueryWrapper wrapper = new QueryWrapper<Post>()
                .eq(categoryId != null, "category_id", categoryId)
                .eq(userId != null, "user_id", userId)
                .eq(level == 0, "level", 0)
                .gt(level > 0, "level", 0)
                .orderByDesc(order != null, order);
        return postMapper.selectPosts(page, wrapper);
    }

    @Override
    public PostVO getPostDetail(Long id) {
        QueryWrapper<Post> wrapper = new QueryWrapper<Post>().eq("p.id", id);
        return postMapper.selectOnePost(wrapper);
    }

    /**
     * 初始化本周热议
     */
    @Override
    public void initWeekRank() {
        //获取7天的文章
        List<Post> posts = this.list(new QueryWrapper<Post>()
                .ge("created", DateUtil.offsetDay(new Date(), -6))
                .select("id, title, user_id, comment_count, view_count, created")
        );
        //初始化文章的总评论量
        for (Post post : posts) {
            //key是day:rank:20200615
            String key = Constant.DAY_RANK_KEY_PREFIX + DateUtil.format(post.getCreated(), DatePattern.PURE_DATE_FORMAT);
            /**
             * 本周热议榜的数据结构：
             *      day:rank:20200615  value: 1(postId) scored: 10(commentCount)
             */
            redisUtil.zSet(key, post.getId(), post.getCommentCount());
            //7天后自动过期(文章发表日期为18号， 过期时间 = 7 - (22 - 18) = 3)
            long between = DateUtil.between(new Date(), post.getCreated(), DateUnit.DAY);
            long expireTime = (7 - between) * 24 * 60 * 60;
            //设置key的有效时间
            redisUtil.expire(key, expireTime);

            //缓存文章的基本信息（postId，标题，评论数量，作者信息）
            this.cacheSavePostInfo(post, expireTime);
        }
        //做并集
        this.unionCommentLast7DaysForRank();
    }

    /**
     * 添加评论时同步操作redis中的本周热议
     * @param postId
     * @param isIncrease
     */
    @Override
    public void increaseCommentCountAndUnionForRank(long postId, boolean isIncrease) {
        if(redisUtil.zHasValue(Constant.WEEK_RANK_KEY, postId)){
            //添加redis中当天的评论数量
            String key = Constant.DAY_RANK_KEY_PREFIX + DateUtil.format(new Date(), DatePattern.PURE_DATE_FORMAT);
            redisUtil.zIncrementScore(key, postId, isIncrease ? 1 : -1);

            //获取Post
            Post post = this.getById(postId);

            //设置过期时间
            long between = DateUtil.between(new Date(), post.getCreated(), DateUnit.DAY);
            long expireTime = (7 - between) * 24 * 60 * 60;

            //缓存文章信息
            cacheSavePostInfo(post, expireTime);

            //同步更新week:rank
            unionCommentLast7DaysForRank();
        }
    }

    @Override
    public void putViewCount(PostVO postVO) {
        String key = Constant.RANK_POST_KEY_PREFIX + postVO.getId();
        //1 从缓存中获取阅读量
        Integer viewCount = (Integer) redisUtil.hget(key, "post:viewCount");
        //2 如果没有，从postVo中获取，再加一
        if(viewCount != null){
            postVO.setViewCount(viewCount + 1);
        }else{
            postVO.setViewCount(postVO.getViewCount() + 1);
        }
        //3 同步缓存
        redisUtil.hset(key, "post:viewCount", postVO.getViewCount());
    }

    @Override
    public IPage<PostVO> getCollectionPagesByUserId(Page page, Long userId) {
        return this.page(page, new QueryWrapper<Post>()
                .inSql("id", "select post_id from user_collection where user_id = " + userId));
    }

    @Override
    public int getPostNumByPostId(Long postId) {
        return this.count(new QueryWrapper<Post>()
                .eq("id", postId)
        );
    }

    @Override
    @Transactional
    public Long submitPost(PostVO postVO, Long userId) {
        if(postVO == null){
            throw new RuntimeException("提交的博客信息不能为空！");
        }
        Long id = postVO.getId();
        Post post = new Post();
        if(id == null){
            //新增
            post.setTitle(postVO.getTitle());
            post.setContent(postVO.getContent());
            post.setEditMode("0");
            post.setCategoryId(postVO.getCategoryId());
            post.setUserId(userId);
            post.setVoteUp(0);
            post.setVoteDown(0);
            post.setViewCount(0);
            post.setCommentCount(0);
            post.setRecommend(false);
            post.setLevel(0);
            post.setCreated(new Date());
            post.setModified(new Date());
            this.save(post);
        }else{
            //修改
            post = this.getById(postVO.getId());
            post.setCategoryId(postVO.getCategoryId());
            post.setTitle(postVO.getTitle());
            post.setContent(postVO.getContent());
            post.setModified(new Date());
            this.updateById(post);
        }
        return post.getId();
    }

    @Override
    @Transactional
    public Boolean deletePost(Long postId, Long userId) {
        if(postId == null){
            return false;
        }
        Post post = this.getById(postId);
        if(post == null){
            return false;
        }
        if(userId.compareTo(post.getUserId()) != 0 && userId.compareTo(1L) != 0){
            return false;
        }
        Long authorId = post.getUserId();
        //删除收藏的文章
        userCollectionService.removeCollection(authorId, postId);
        //删除文章的评论
        commentService.removeByMap(MapUtil.of("post_id", postId));
        //删除用户的消息
        userMessageService.removeAllMessageByPostId(postId);
        return this.removeById(postId);
    }

    @Override
    @Transactional
    public void updatePostRecommend(Long postId, Integer rank) {
        if(postId == null){
            throw new RuntimeException("文章错误!");
        }
        Post post = this.getById(postId);
        if (post == null){
            throw new RuntimeException("文章已被删除！");
        }
        if(Integer.compare(rank, 0) == 0){
            post.setRecommend(false);
        }else{
            post.setRecommend(true);
        }
        post.setModified(new Date());
        this.updateById(post);
    }

    @Override
    public void updatePostLevel(Long postId, Integer rank) {
        if(postId == null){
            throw new RuntimeException("文章错误!");
        }
        Post post = this.getById(postId);
        if (post == null){
            throw new RuntimeException("文章已被删除！");
        }
        post.setLevel(rank);
        post.setModified(new Date());
        this.updateById(post);
    }

    @Override
    @Transactional
    public void addComment(Long postId, String content, Long userId) {
        Post post = this.getById(postId);
        if(post == null){
            throw new RuntimeException("文章已被删除!");
        }
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setContent(content);
        comment.setLevel(0);
        comment.setUserId(userId);
        comment.setVoteDown(0);
        comment.setVoteUp(0);
        comment.setCreated(new Date());
        comment.setModified(new Date());
        commentService.save(comment);

        //文章的评论数加1
        post.setCommentCount(post.getCommentCount() + 1);
        this.updateById(post);

        //本周热议板块的评论的更新
        this.increaseCommentCountAndUnionForRank(postId, true);

        //通知作者，有人评论你的文章
        //除了用户自己的评论不需要去发送消息
        if(!comment.getUserId().equals(post.getUserId())){
            UserMessage userMessage = new UserMessage();
            userMessage.setFrouserId(userId);
            userMessage.setPostId(postId);
            userMessage.setCommentId(comment.getId());
            userMessage.setToUserId(post.getUserId());
            userMessage.setContent(content);
            userMessage.setType(1);
            userMessage.setCreated(new Date());
            userMessage.setModified(new Date());
            userMessage.setStatus(0);
            userMessageService.save(userMessage);

            //利用websocket即时通知用户
            webSocketService.sendMessageToUser(userMessage.getToUserId());
        }

        //如果回复别人的评论, 需要对content进行截取
        if(content.startsWith("@")){
            String username = content.substring(1, content.indexOf(' '));
            System.out.println(username);
            User user = userService.getUserByUsername(username);
            if(user != null){
                UserMessage userMessage = new UserMessage();
                userMessage.setFrouserId(userId);
                userMessage.setPostId(postId);
                userMessage.setCommentId(comment.getId());
                userMessage.setToUserId(user.getId());
                userMessage.setContent(content);
                userMessage.setType(2);
                userMessage.setCreated(new Date());
                userMessage.setModified(new Date());
                userMessage.setStatus(0);
                userMessageService.save(userMessage);
            }
        }
    }

    @Override
    public Boolean increasePostCommentLike(Long id, Boolean ok) {
        if(id == null || ok == null){
            return false;
        }
        Comment comment = commentService.getById(id);
        if(ok){
            comment.setVoteUp(comment.getVoteUp() + 1);
        }
        commentService.updateById(comment);
        return true;
    }

    @Override
    @Transactional
    public Long deleteComment(Long id, Long userId) {
        if(id == null){
            return null;
        }
        Comment comment = commentService.getById(id);
        Long postId = comment.getPostId();
        Post post = this.getById(postId);
        //只有博客的作者才能够删除评论
        if(!post.getUserId().equals(userId)){
            return null;
        }
        commentService.removeById(id);

        post.setCommentCount(post.getCommentCount() - 1);
        this.updateById(post);

        //本周热议评论减1
        this.increaseCommentCountAndUnionForRank(postId, false);
        return postId;
    }

    /**
     * 缓存文章基本信息
     * @param post
     * @param expireTime
     */
    private void cacheSavePostInfo(Post post, long expireTime) {
        String key = Constant.RANK_POST_KEY_PREFIX + post.getId();
        boolean hasKey = redisUtil.hasKey(key);
        if(!hasKey){
            redisUtil.hset(key, "post:id", post.getId(), expireTime);
            redisUtil.hset(key, "post:title", post.getTitle(), expireTime);
            redisUtil.hset(key, "post:commentCount", post.getCommentCount(), expireTime);
            redisUtil.hset(key, "post:viewCount", post.getViewCount(), expireTime);
        }
    }

    /**
     * 文章每日评论并集，合并评论数量
     */
    private void unionCommentLast7DaysForRank() {
        String key = Constant.RANK_POST_KEY_PREFIX + DateUtil.format(new Date(), DatePattern.PURE_DATE_FORMAT);
        List<String> otherKeys = new ArrayList<>();
        for (int i = -6; i <= 0; i++){
            String otherKey = Constant.DAY_RANK_KEY_PREFIX +
                    DateUtil.format(DateUtil.offsetDay(new Date(), i), DatePattern.PURE_DATE_FORMAT);
            otherKeys.add(otherKey);
        }
        redisUtil.zUnionAndStore(key, otherKeys, Constant.WEEK_RANK_KEY);
    }
}
