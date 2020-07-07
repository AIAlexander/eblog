package com.alex.service;

import com.alex.entity.UserMessage;
import com.alex.vo.UserMessageVO;
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
public interface UserMessageService extends IService<UserMessage> {

    IPage<UserMessageVO> getMessagePageByToUserId(Page page, Long userId);


    Boolean removeMessageById(Long id, Long userId, Boolean all);
}
