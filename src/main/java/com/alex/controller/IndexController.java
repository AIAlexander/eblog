package com.alex.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author wsh
 * @date 2020-06-11
 */
@Controller
public class IndexController extends BaseController{

    @RequestMapping({"","/","/index"})
    public String index(){
        Integer pageNum = ServletRequestUtils.getIntParameter(req, "pn", 1);
        Integer size = ServletRequestUtils.getIntParameter(req, "size", 2);
        Page page = new Page(pageNum, size);

        //入参：分页信息，分类信息，用户信息，是否置顶，是否精选，是否排序
        IPage result = postService.getPostByPage(page, null, null, null, null, "created");

        req.setAttribute("pageData", result);
        req.setAttribute("currentCategoryId", 0);

        return "index";
    }
}
