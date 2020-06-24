package com.alex.config;

import com.alex.entity.Category;
import com.alex.service.CategoryService;
import com.alex.service.PostService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.util.List;

/**
 * @author wsh
 * @date 2020-06-15
 *
 */
@Component
public class ContextStartUp implements ApplicationRunner, ServletContextAware {

    @Autowired
    private CategoryService categoryService;

    private ServletContext servletContext;

    @Autowired
    private PostService postService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Category> categories = categoryService.list(new QueryWrapper<Category>()
                .eq("status", 0));
        servletContext.setAttribute("categories", categories);

        //初始化首页的本周热议板块
        postService.initWeekRank();
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
