package com.alex.config;

import com.alex.template.PostsTemplate;
import com.alex.template.TimeTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author wsh
 * @date 2020-06-16
 * Freemarker的配置文件
 */

@Configuration
public class FreemarkerConfig {

    @Autowired
    private freemarker.template.Configuration configuration;

    @Autowired
    private PostsTemplate postsTemplate;

    @PostConstruct
    public void setUp(){
        //将自定义的时间组件注入到Freemarker配置中
        configuration.setSharedVariable("time", new TimeTemplate());
        configuration.setSharedVariable(postsTemplate.getName(), postsTemplate);
    }
}
