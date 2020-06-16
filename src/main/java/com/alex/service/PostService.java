package com.alex.service;

import com.alex.entity.Post;
import com.alex.vo.PostVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;


import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author alex
 * @since 2020-06-15
 */
public interface PostService extends IService<Post> {

    IPage<PostVO> getPostByPage(Page page, Long categoryId, Long userId, Integer level, Boolean recommend, String order);

    PostVO getPostDetail(Long id);
}
