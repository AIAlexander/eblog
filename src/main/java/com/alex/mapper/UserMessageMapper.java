package com.alex.mapper;

import com.alex.entity.UserMessage;
import com.alex.vo.UserMessageVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author alex
 * @since 2020-06-15
 */
@Component
public interface UserMessageMapper extends BaseMapper<UserMessage> {
    IPage<UserMessageVO> getMessagePageByToUserId(Page page, @Param(Constants.WRAPPER) QueryWrapper<UserMessage> wrapper);

    @Transactional
    @Update("update user_message set status = 1 ${ew.customSqlSegment}")
    void updateMessageStatusByIds(@Param(Constants.WRAPPER) QueryWrapper<UserMessage> wrapper);
}
