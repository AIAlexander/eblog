package com.alex.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alex.common.Constant;
import com.alex.common.Result;
import com.alex.util.ValidationUtil;
import com.alex.vo.UserVO;
import com.google.code.kaptcha.Producer;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author wsh
 * @date 2020-06-26
 */
@Controller
public class AuthController extends BaseController{

    @Autowired
    private Producer producer;

    @GetMapping("/captcha.jpg")
    public void kaptcha(HttpServletResponse response) throws IOException {
        //验证码
        String text = producer.createText();
        BufferedImage image = producer.createImage(text);
        //保存验证码到Session中
        req.getSession().setAttribute(Constant.KAPTCHA_SESSION_KEY, text);
        response.setHeader("Cache-control", "no-store, no-cache");
        response.setContentType("image/jpeg");
        ServletOutputStream outputStream = response.getOutputStream();
        ImageIO.write(image, "jpg", outputStream);
    }

    @GetMapping(value = "**/login")
    public String login(){
        return "/auth/login";
    }

    @GetMapping(value = "**/register")
    public String register(){
        return "/auth/reg";
    }

    @ResponseBody
    @PostMapping("/register")
    public Result doRegister(UserVO userVO){
        ValidationUtil.ValidResult validResult = ValidationUtil.validateBean(userVO);
        if(validResult.hasErrors()){
            return Result.fail(validResult.getErrors());
        }
        if (!userVO.getPassword().equals(userVO.getRepass())){
            return Result.fail("两次输入密码不相同");
        }
        //获取Session中的验证码
        String captchaText = (String) req.getSession().getAttribute(Constant.KAPTCHA_SESSION_KEY);
        if(userVO.getVercode() == null ||
                !captchaText.equalsIgnoreCase(userVO.getVercode())){
            return Result.fail("验证码输入不正确!");
        }

        //完成注册
        Result result = userService.register(userVO);

        return result.action("/login");
    }

    @PostMapping("/login")
    @ResponseBody
    public Result doLogin(UserVO userVO){
        if(StrUtil.isEmpty(userVO.getEmail()) || StrUtil.isEmpty(userVO.getPassword())){
            return Result.fail("邮箱或者密码不能为空!");
        }
        UsernamePasswordToken token = new UsernamePasswordToken(userVO.getEmail(), SecureUtil.md5(userVO.getPassword()));
        try{
            SecurityUtils.getSubject().login(token);
        } catch (AuthenticationException e){
            if(e instanceof UnknownAccountException){
                return Result.fail("用户不存在");
            } else if(e instanceof LockedAccountException){
                return Result.fail("用户被禁用");
            } else if(e instanceof IncorrectCredentialsException){
                return Result.fail("密码错误");
            } else{
                return Result.fail("用户认证失败");
            }
        }
        return Result.success().action("/");
    }

    @RequestMapping("/user/logout")
    public String logout(){
        SecurityUtils.getSubject().logout();
        return "redirect:/";
    }
}
