package com.alex.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alex.entity.UserMessage;
import com.alex.mapper.UserMessageMapper;
import com.alex.service.UserMessageService;
import com.alex.vo.UserMessageVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author alex
 * @since 2020-06-15
 */
@Service
public class UserMessageServiceImpl extends ServiceImpl<UserMessageMapper, UserMessage> implements UserMessageService {

    @Autowired
    UserMessageMapper userMessageMapper;

    @Override
    public IPage<UserMessageVO> getMessagePageByToUserId(Page page, Long userId) {
        QueryWrapper<UserMessage> wrapper = new QueryWrapper<>();
        wrapper.eq("to_user_id", userId)
                .orderByDesc("created");
        IPage<UserMessageVO> userMessageVOPage = userMessageMapper.getMessagePageByToUserId(page, wrapper);

        //将消息批量改成已读状态
        List<Long> ids = new ArrayList<>();
        for (UserMessageVO userMessageVO : userMessageVOPage.getRecords()){
            if(userMessageVO.getStatus() == 0){
                ids.add(userMessageVO.getId());
            }
        }
        updateMessageStatusByIds(ids);

        return userMessageVOPage;
    }

    @Override
    public Boolean removeMessageById(Long id, Long userId, Boolean all) {
        return this.remove(new QueryWrapper<UserMessage>()
                .eq("to_user_id", userId)
                .eq(!all, "id", id)
        );
    }

    @Override
    public Integer getNonReadMessageNumByUserId(Long profileId) {
        return this.count(new QueryWrapper<UserMessage>()
                .eq("to_user_id", profileId)
                .eq("status", "0")
        );
    }

    @Override
    public Boolean removeAllMessageByPostId(Long postId) {
        return  this.remove(new QueryWrapper<UserMessage>()
                .eq("post_id", postId));
    }

    public void updateMessageStatusByIds(List<Long> ids){
        if (CollectionUtil.isEmpty(ids)){
            return;
        }
        userMessageMapper.updateMessageStatusByIds(new QueryWrapper<UserMessage>()
                .in("id", ids)
        );
    }
}
