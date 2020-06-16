package com.alex.vo;

import com.alex.entity.Post;
import lombok.Data;

/**
 * @author wsh
 * @date 2020-06-15
 */
@Data
public class PostVO extends Post {
    private Long authorId;
    private String authorName;
    private String authorAvatar;
    private String categoryName;

}
