package com.alex.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alex.common.Result;
import com.alex.entity.Post;
import com.alex.entity.User;
import com.alex.shiro.AccountProfile;
import com.alex.util.UploadUtil;
import com.alex.vo.UserMessageVO;
import com.alex.vo.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author wsh
 * @date 2020-07-01
 */
@Controller
public class UserController extends BaseController {

    @Autowired
    UploadUtil uploadUtil;

    @GetMapping("/user/home")
    public String home(){

        //从shiro中获取登录的用户信息
        User user = userService.getById(getProfileId());

        //根据用户id获取发表的博客信息
        List<Post> posts = postService.list(new QueryWrapper<Post>()
                .eq("user_id", getProfileId())
                .gt("created", DateUtil.offsetDay(new Date(), -30))
                .orderByDesc("created")
        );
        req.setAttribute("user", user);
        req.setAttribute("posts", posts);
        return "user/home";
    }

    @GetMapping("/user/set")
    public String set(){

        //从shiro中获取登录的用户信息
        User user = userService.getById(getProfileId());

        req.setAttribute("user", user);
        return "user/set";
    }

    @GetMapping("/user/index")
    public String index(){

        //从shiro中获取登录的用户信息
//        User user = userService.getById(getProfileId());

//        req.setAttribute("user", user);
        return "user/index";
    }

    //设置基本信息与设置头像并用接口
    @PostMapping("/user/set")
    @ResponseBody
    public Result doSet(UserVO userVO){

        //如果传入的头像不为空，说明是更新头像
        if(StrUtil.isNotBlank(userVO.getAvatar())){
            User temp = userService.getById(getProfileId());
            temp.setAvatar(userVO.getAvatar());
            userService.updateById(temp);
            AccountProfile profile = getAccountProfile();
            profile.setAvatar(userVO.getAvatar());

            //同步更新缓存中的用户数据
            SecurityUtils.getSubject().getSession().setAttribute("profile", profile);

            return Result.success().action("/user/set#avatar");
        }

        //否则就是更新基本信息
        if(StrUtil.isBlank(userVO.getUsername())){
            return Result.fail("昵称不能为空");
        }
        Integer count = userService.getUserCountByIdAndUsername(getProfileId(), userVO.getUsername());
        System.out.println(count);
        if(count > 0){
            return Result.fail("该昵称已被占用");
        }
        //更新数据库中用户的数据
        userService.setInfo(getProfileId(), userVO);

        //同步更新shiro缓存中的用户数据
        AccountProfile profile = getAccountProfile();
        profile.setUsername(userVO.getUsername());
        profile.setSign(userVO.getSign());
        //同步更新缓存中的用户数据
        SecurityUtils.getSubject().getSession().setAttribute("profile", profile);

        return Result.success().action("/user/set#info");
    }

    @PostMapping("/user/upload")
    @ResponseBody
    public Result uploadAvatar(@RequestParam(value = "file") MultipartFile file) throws IOException {
        return uploadUtil.upload(UploadUtil.TYPE_AVATAR, file);
    }

    @PostMapping("/user/repass")
    @ResponseBody
    public Result repass(String nowpass, String pass, String repass){
        if(StrUtil.isEmpty(nowpass) || StrUtil.isEmpty(pass) || StrUtil.isEmpty(repass)){
            return Result.fail("提交的信息不能为空！");
        }
       if(!StrUtil.equals(pass, repass)){
           return Result.fail("两次输入的密码不一致！");
       }
       return userService.repass(nowpass, pass, getProfileId());

    }

    @GetMapping("/user/post")
    @ResponseBody
    public Result post(){

        IPage page = postService.
                getPostByPage(createPage(), null, getProfileId(), null, null, null);

        return Result.success(page);
    }

    @GetMapping("/user/collection")
    @ResponseBody
    public Result collection(){
        IPage page = postService.getCollectionPagesByUserId(createPage(), getProfileId());
        return Result.success(page);
    }

    @GetMapping("/user/message")
    public String message(){

        IPage<UserMessageVO> page = userMessageService.getMessagePageByToUserId(createPage(), getProfileId());

        req.setAttribute("pageData", page);

        return "user/message";
    }

    @PostMapping("/message/remove")
    @ResponseBody
    public Result removeMessage(Long id, @RequestParam(defaultValue = "false") Boolean all){

        Boolean res = userMessageService.removeMessageById(id, getProfileId(), all);

        return res ? Result.success() : Result.fail("删除失败");
    }

    @ResponseBody
    @PostMapping("/message/nums")
    public Map getMessageNum(){
        Integer messageNum = userMessageService.getNonReadMessageNumByUserId(getProfileId());
        return MapUtil.builder("status", 0).put("count", messageNum).build();
    }

}
