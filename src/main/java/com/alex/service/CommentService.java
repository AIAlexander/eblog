package com.alex.service;

import com.alex.entity.Comment;
import com.alex.vo.CommentVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author alex
 * @since 2020-06-15
 */
public interface CommentService extends IService<Comment> {

    IPage<CommentVO> getComments(Page page, Long postId, Long userId, String order);
}
