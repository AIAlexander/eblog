package com.alex.mapper;

import com.alex.entity.Post;
import com.alex.vo.PostVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;


import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author alex
 * @since 2020-06-15
 */
@Component
public interface PostMapper extends BaseMapper<Post> {

    IPage<PostVO> selectPosts(Page page, @Param(Constants.WRAPPER) QueryWrapper wrapper);
}
