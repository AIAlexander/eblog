package com.alex.vo;

import com.alex.entity.Comment;
import lombok.Data;

/**
 * @author wsh
 * @date 2020-06-22
 */
@Data
public class CommentVO extends Comment {
    private Long authorId;
    private String authorName;
    private String authorAvatar;
}
