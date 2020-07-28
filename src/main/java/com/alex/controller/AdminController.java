package com.alex.controller;

import com.alex.common.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author wsh
 * @date 2020-07-27
 * 超级管理员的操作
 */
@Controller
@RequestMapping("/admin")
public class AdminController extends BaseController{

    private static final String RECOMMEND = "recommend";
    private static final String STICK = "stick";


    /**
     * 超级管理员置顶，加精博客文章
     * @param id
     * @param rank 0表示取消操作，1表示设置操作
     * @param field recommend表示加精，stick表示置顶
     * @return
     */
    @ResponseBody
    @PostMapping("/set")
    public Result set(Long id, Integer rank, String field) {
        //field为status时表示加精，field为stick时表示置顶
        if(StringUtils.isEmpty(field)){
            return Result.fail("操作不能为空!");
        }
        if (StringUtils.equals(RECOMMEND, field)){
            //加精
            postService.updatePostRecommend(id, rank);
        }else if(StringUtils.equals(STICK, field)){
            //置顶
            postService.updatePostLevel(id, rank);
        }else{
            return Result.fail("操作有误！");
        }

        return Result.success();
    }

}
