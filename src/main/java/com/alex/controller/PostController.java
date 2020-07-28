package com.alex.controller;

import cn.hutool.core.map.MapUtil;
import com.alex.common.Result;
import com.alex.entity.Post;
import com.alex.util.ValidationUtil;
import com.alex.vo.CommentVO;
import com.alex.vo.PostVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * @author wsh
 */
@Controller
public class PostController extends BaseController{

    @GetMapping("/category/{id:\\d*}")
    public String detail(@PathVariable(name = "id") Long id){

        Integer pageNum = ServletRequestUtils.getIntParameter(req, "pn", 1);

        req.setAttribute("currentCategoryId", id);
        req.setAttribute("pn", pageNum);

        return "post/category";
    }

    @GetMapping("/post/{id:\\d*}")
    public String post(@PathVariable(name = "id") Long id){

        PostVO postVO = postService.getPostDetail(id);
        Assert.notNull(postVO, "文章已被删除");

        postService.putViewCount(postVO);

        //获取博客详情页面的评论列表(1分页，2文章id，3用户id，排序)
        IPage<CommentVO> results = commentService.getComments(createPage(), postVO.getId(), null, "created");

        req.setAttribute("post", postVO);
        req.setAttribute("currentCategoryId", postVO.getCategoryId());
        req.setAttribute("pageData", results);
        return "/post/detail";
    }

    @GetMapping("/post/edit")
    public String edit(){
        String id = req.getParameter("id");
        if(!StringUtils.isEmpty(id)){
            Post post = postService.getById(id);
            Assert.isTrue(post != null, "该帖子已被删除");
            Assert.isTrue(Long.compare(getProfileId(), post.getUserId()) == 0 , "没有权限操作此文章");
            req.setAttribute("post", post);
        }
        req.setAttribute("categories", categoryService.list());
        return "/post/edit";
    }

    @PostMapping("/post/submit")
    @ResponseBody
    public Result submit(PostVO postVO){
        ValidationUtil.ValidResult validResult = ValidationUtil.validateBean(postVO);
        if(validResult.hasErrors()){
            return Result.fail(validResult.getErrors());
        }
        Long postId = postService.submitPost(postVO, getProfileId());

        return Result.success().action("/post/" + postId);
    }

    @PostMapping("/post/delete")
    @ResponseBody
    public Result deletePost(Long id){
        Long userId = getProfileId();
        Boolean flag = postService.deletePost(id, userId);

        if(flag){
            return Result.success();
        }else{
            return Result.fail("删除失败");
        }
    }

    @PostMapping("/post/reply")
    @ResponseBody
    public Result reply(Long postId, String content){
        Assert.notNull(postId, "找不到对应的文章!");
        Assert.hasLength(content, "评论的内容不能为空！");
        Long userId = getProfileId();
        postService.addComment(postId, content, userId);

        return Result.success().action("/post/" + postId);
    }

    @PostMapping("/post/like")
    @ResponseBody
    public Result like(Long id, Boolean ok){

        Boolean flag = postService.increasePostCommentLike(id, ok);
        if (flag){
            return Result.success();
        }else{
            return Result.fail("点赞失败！");
        }
    }


    /**
     * 判断用户是否收藏文章
     * @param pid
     * @return
     */
    @PostMapping("/collection/find")
    @ResponseBody
    public Result findCollection(Long pid){

        int collectionNum = collectionService.getCollectionNumByPostId(getProfileId(), pid);

        return Result.success(MapUtil.of("collection", collectionNum > 0));
    }

    @PostMapping("/collection/add")
    @ResponseBody
    public Result addCollection(Long pid){

        Boolean flag = collectionService.addCollection(getProfileId(), pid);

        return flag ? Result.success() : Result.fail("收藏失败");
    }

    @PostMapping("/collection/remove")
    @ResponseBody
    public Result removeCollection(Long pid){
        Boolean flag = collectionService.removeCollection(getProfileId(), pid);
        return flag ? Result.success() : Result.fail("取消收藏失败");
    }
}
