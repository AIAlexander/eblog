package com.alex.controller;

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
        Integer size = ServletRequestUtils.getIntParameter(req, "size", 2);
        Page page = new Page(pageNum, size);

        //入参：分页信息，分类信息，用户信息，是否置顶，是否精选，是否排序
        IPage result = postService.getPostByPage(page, null, null, null, null, "created");

        req.setAttribute("currentCategoryId", id);
        req.setAttribute("pageData", result);
        return "post/category";
    }

    @GetMapping("/post/{id:\\d*}")
    public String post(@PathVariable(name = "id") Long id){

        PostVO postVO = postService.getPostDetail(id);
        Assert.notNull(postVO, "文章已被删除");
        return "/post/detail";
    }
}
