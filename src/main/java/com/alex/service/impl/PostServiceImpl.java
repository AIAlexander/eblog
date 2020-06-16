package com.alex.service.impl;

import com.alex.entity.Post;
import com.alex.mapper.PostMapper;
import com.alex.service.PostService;
import com.alex.vo.PostVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author alex
 * @since 2020-06-15
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Override
    public IPage<PostVO> getPostByPage(Page page, Long categoryId, Long userId, Integer level, Boolean recommend, String order) {
        if(level == null){
            level = -1;
        }
        //构造搜索条件
        QueryWrapper wrapper = new QueryWrapper<Post>()
                .eq(categoryId != null, "category_id", categoryId)
                .eq(userId != null, "user_id", userId)
                .eq(level == 0, "level", 0)
                .gt(level > 0, "level", 0)
                .orderByDesc(order != null, order);
        return postMapper.selectPosts(page, wrapper);
    }

    @Override
    public PostVO getPostDetail(Long id) {
        QueryWrapper wrapper = new QueryWrapper<Post>().eq("id", id);
        return null;
    }
}
