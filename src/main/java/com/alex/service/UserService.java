package com.alex.service;

import com.alex.common.Result;
import com.alex.entity.User;
import com.alex.shiro.AccountProfile;
import com.alex.vo.UserVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author alex
 * @since 2020-06-15
 */
public interface UserService extends IService<User> {

    Result register(UserVO userVO);

    AccountProfile login(String email, String password);

    Integer getUserCountByIdAndUsername(Long userId, String username);

    Result setInfo(Long id, UserVO userVO);

    Result repass(String nowpass, String pass, Long userId);
}
