package com.alex.vo;

import com.alex.entity.UserMessage;
import lombok.Data;

/**
 * @author wsh
 * @date 2020-07-06
 */
@Data
public class UserMessageVO extends UserMessage {
    private String toUserName;
    private String fromUserName;
    private String postTitle;
    private String commentContent;
}
