package com.alex.controller;

import com.alex.service.CommentService;
import com.alex.service.PostService;
import com.alex.service.UserMessageService;
import com.alex.service.UserService;
import com.alex.shiro.AccountProfile;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
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

    @Autowired
    public UserService userService;

    @Autowired
    public UserMessageService userMessageService;

    public Page createPage(){
        Integer pageNum = ServletRequestUtils.getIntParameter(req, "pn", 1);
        Integer size = ServletRequestUtils.getIntParameter(req, "size", 2);
        return new Page(pageNum, size);
    }

    protected AccountProfile getAccountProfile(){
        return (AccountProfile) SecurityUtils.getSubject().getPrincipal();
    }

    protected Long getProfileId(){
        return  getAccountProfile().getId();
    }
}
