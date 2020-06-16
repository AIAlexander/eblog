package com.alex.controller;

import com.alex.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;

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
}
