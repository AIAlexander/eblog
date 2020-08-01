package com.alex.service.impl;

import com.alex.service.UserMessageService;
import com.alex.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * @author wsh
 * @date 2020-07-31
 */
@Service
public class WebSocketServiceImpl implements WebSocketService {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private UserMessageService userMessageService;

    @Override
    public void sendMessageToUser(Long toUserId) {
        int count = userMessageService.getNonReadMessageNumByUserId(toUserId);
        //通过websocket进行发生通知
        simpMessagingTemplate.convertAndSendToUser(toUserId.toString(), "/messCount", count);
    }
}
