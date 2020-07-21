package com.alex.common.exception;

import cn.hutool.json.JSONUtil;
import com.alex.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author wsh
 * @date 2020-07-20
 * 全局异常处理
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ModelAndView handler(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException {
        log.error(e.getMessage());
        e.printStackTrace();
        //判断是否为ajax的请求，弹窗显示未登录
        String header = request.getHeader("X-Requested-With");
        if(header != null && "XMLHttpRequest".equals(header)){
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print(JSONUtil.toJsonStr(Result.fail(e.getMessage())));
            return null;
        }
        //web请求处理方式
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("message", e.getMessage());
        return modelAndView;
    }
}
