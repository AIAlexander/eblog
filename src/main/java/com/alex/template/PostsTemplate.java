package com.alex.template;

import com.alex.common.templates.DirectiveHandler;
import com.alex.common.templates.TemplateDirective;
import com.alex.service.PostService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import freemarker.template.TemplateModelException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wsh
 * @date 2020-06-16
 */
@Component
public class PostsTemplate extends TemplateDirective {

    @Autowired
    private PostService postService;

    @Override
    public String getName(){
        return "posts";
    }

    @Override
    public void execute(DirectiveHandler handler) throws Exception {
        Integer level = handler.getInteger("level");
        Integer pn = handler.getInteger("pn", 1);
        Integer size = handler.getInteger("size", 2);
        Long categoryId = handler.getLong("categoryId");
        IPage page = postService.getPostByPage(new Page(pn, size), categoryId,
                null, level, null, "created");
        //返回给Freemarker进行渲染
        handler.put(RESULTS, page).render();
    }


}
