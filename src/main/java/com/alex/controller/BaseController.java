package com.alex.controller;

import com.alex.service.CommentService;
import com.alex.service.PostService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wsh
 * @date 2020-06-15
 */
public class BaseController {
    @Autowired
    public HttpServletRequest req;

    @Autowired
    public PostService postService;

    @Autowired
    public CommentService commentService;

    public Page createPage(){
        Integer pageNum = ServletRequestUtils.getIntParameter(req, "pn", 1);
        Integer size = ServletRequestUtils.getIntParameter(req, "size", 2);
        return new Page(pageNum, size);
    }
}
