package com.alex.shiro;

import com.alex.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author wsh
 * @date 2020-06-27
 */
@Component
public class AccountRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        AccountProfile accountProfile = (AccountProfile) principalCollection.getPrimaryPrincipal();

        //给id为1的用户赋予admin的角色
        if(accountProfile.getId() == 1){
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            info.addRole("admin");
            return info;
        }
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        AccountProfile accountProfile = userService.login(usernamePasswordToken.getUsername(),
                String.valueOf(usernamePasswordToken.getPassword()));
        //将用户信息放到session里
        SecurityUtils.getSubject().getSession().setAttribute("profile", accountProfile);
        //将返回的用户信息放到缓存中返回
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(accountProfile,
                token.getCredentials(), getName());
        return info;
    }
}
