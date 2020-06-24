package com.alex.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alex.entity.Post;
import com.alex.mapper.PostMapper;
import com.alex.service.PostService;
import com.alex.util.RedisUtil;
import com.alex.vo.PostVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            String key = "day:rank:" + DateUtil.format(post.getCreated(), DatePattern.PURE_DATE_FORMAT);
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
     * 缓存文章基本信息
     * @param post
     * @param expireTime
     */
    private void cacheSavePostInfo(Post post, long expireTime) {
        String key = "rank:post:" + post.getId();
        boolean hasKey = redisUtil.hasKey(key);
        if(!hasKey){
            redisUtil.hset(key, "post:id", post.getId(), expireTime);
            redisUtil.hset(key, "post:title", post.getTitle(), expireTime);
            redisUtil.hset(key, "post:commentCount", post.getCommentCount(), expireTime);
        }
    }

    /**
     * 文章每日评论并集，合并评论数量
     */
    private void unionCommentLast7DaysForRank() {
        String destKey = "week:rank";
        String key = "day:rank:" + DateUtil.format(new Date(), DatePattern.PURE_DATE_FORMAT);
        List<String> otherKeys = new ArrayList<>();
        for (int i = -6; i < 0; i++){
            String otherKey = "day:rank:" +
                    DateUtil.format(DateUtil.offsetDay(new Date(), i), DatePattern.PURE_DATE_FORMAT);
            otherKeys.add(otherKey);
        }
        redisUtil.zUnionAndStore(key, otherKeys, destKey);
    }
}
