package com.alex.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alex.common.Result;
import com.alex.entity.User;
import com.alex.mapper.UserMapper;
import com.alex.service.UserService;
import com.alex.shiro.AccountProfile;
import com.alex.vo.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author alex
 * @since 2020-06-15
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Transactional
    @Override
    public Result register(UserVO userVO) {
        //判断邮箱和昵称是否唯一
        Integer count = this.count(new QueryWrapper<User>()
                .eq("email", userVO.getEmail())
                .or()
                .eq("username", userVO.getUsername()));
        if(count > 0) {
            return Result.fail("用户名或邮箱已被注册");
        }
        User user = new User();
        user.setEmail(userVO.getEmail());
        user.setUsername(userVO.getUsername());
        user.setPassword(SecureUtil.md5(userVO.getPassword()));
        user.setCreated(new Date());
        user.setPoint(0);
        user.setVipLevel(0);
        user.setCommentCount(0);
        user.setPostCount(0);
        user.setGender("0");
        user.setAvatar("/res/images/avatar/default.png");
        this.save(user);

        return Result.success();
    }

    @Override
    public AccountProfile login(String email, String password) {
        User user = this.getOne(new QueryWrapper<User>().eq("email", email));
        if(user == null){
            throw new UnknownAccountException();
        }
        if(!user.getPassword().equals(password)){
            throw new IncorrectCredentialsException();
        }
        user.setLasted(new Date());
        this.updateById(user);

        AccountProfile profile = new AccountProfile();
        BeanUtil.copyProperties(user, profile);
        return profile;
    }

    @Override
    public Integer getUserCountByIdAndUsername(Long userId, String username) {
        if(StrUtil.isBlank(username)){
            throw new RuntimeException("用户名不能为空");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        if(!StrUtil.isBlank(username)){
            queryWrapper.ne("id", userId);
        }
        return this.count(queryWrapper);
    }

    @Override
    public Result setInfo(Long id, UserVO userVO) {
        //更新数据库中用户的数据
        User user = this.getById(id);
        user.setGender(userVO.getGender());
        user.setEmail(userVO.getEmail());
        user.setSign(userVO.getSign());
        user.setUsername(userVO.getUsername());
        this.updateById(user);

        //同步更新shiro中的用户数据
        return Result.success();
    }

    @Override
    public Result repass(String nowpass, String pass, Long userId) {
        User user = this.getById(userId);
        String nowpassMd5 = SecureUtil.md5(nowpass);
        if(!StrUtil.equals(nowpassMd5, user.getPassword())){
            return Result.fail("密码不正确");
        }
        user.setPassword(SecureUtil.md5(pass));
        this.updateById(user);
        return Result.success().action("/user/set#pass");
    }

    @Override
    public User getUserByUsername(String username) {
        return this.getOne(new QueryWrapper<User>()
                .eq("username", username));
    }
}
