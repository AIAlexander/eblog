package com.alex.controller;

import com.alex.vo.CommentVO;
import com.alex.vo.PostVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


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

        //获取博客详情页面的评论列表(1分页，2文章id，3用户id，排序)
        IPage<CommentVO> results = commentService.getComments(createPage(), postVO.getId(), null, "created");

        req.setAttribute("post", postVO);
        req.setAttribute("currentCategoryId", postVO.getCategoryId());
        req.setAttribute("pageData", results);
        return "/post/detail";
    }
}
