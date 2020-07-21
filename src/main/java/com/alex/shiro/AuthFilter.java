package com.alex.shiro;

import cn.hutool.json.JSONUtil;
import com.alex.common.Result;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.filter.authc.UserFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author wsh
 * @date 2020-07-20
 * shiro的过滤器
 * 在未登录下，判断是否是ajax请求，如果是弹出显示
 */
public class AuthFilter extends UserFilter {

    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        //判断是否为ajax的请求，弹窗显示未登录
        String header = httpServletRequest.getHeader("X-Requested-With");
        if(header != null && "XMLHttpRequest".equals(header)){
            boolean flag = SecurityUtils.getSubject().isAuthenticated();
            if(!flag){
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().print(JSONUtil.toJsonStr(Result.fail("请先登录！")));
            }
        }else{
            //如果是web的请求，重定向到登录页面
            super.redirectToLogin(request, response);
        }
    }
}
