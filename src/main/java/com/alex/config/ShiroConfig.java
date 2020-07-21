package com.alex.config;

import cn.hutool.core.map.MapUtil;
import com.alex.shiro.AccountRealm;
import com.alex.shiro.AuthFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author wsh
 * @date 2020-06-27
 * Shiro框架配置中心
 */
@Slf4j
@Configuration
public class ShiroConfig {

    @Bean
    public SecurityManager securityManager(AccountRealm accountRealm){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(accountRealm);
        log.info("----------------------SecurityManager注入成功");
        return securityManager;
    }

    /**
     * 配置shiro过滤器组
     * @param securityManager
     * @return
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager){
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(securityManager);
        //设置登录的url和登录成功的url
        factoryBean.setLoginUrl("/login");
        factoryBean.setSuccessUrl("/user/center");
        //配置未授权跳转页面
        factoryBean.setUnauthorizedUrl("/error/403");

        //加入自定义的登录过滤器
        factoryBean.setFilters(MapUtil.of("auth", authFilter()));

        Map<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put("/**", "anon");

        //登录的用户才能够进行操作
        hashMap.put("/user/home", "auth");
        hashMap.put("/user/set", "auth");
        hashMap.put("/user/upload", "auth");
        hashMap.put("/post/edit", "auth");

        hashMap.put("/collection/remove/", "auth");
        hashMap.put("/collection/add/", "auth");
        hashMap.put("/collection/find/", "auth");

        factoryBean.setFilterChainDefinitionMap(hashMap);

        return factoryBean;
    }

    /**
     * 注入过滤器
     * @return
     */
    @Bean
    public AuthFilter authFilter(){
        return new AuthFilter();
    }
}
