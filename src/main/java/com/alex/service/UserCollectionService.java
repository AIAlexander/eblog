package com.alex.service;

import com.alex.entity.UserCollection;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author alex
 * @since 2020-06-15
 */
public interface UserCollectionService extends IService<UserCollection> {

    Integer getCollectionNumByPostId(Long userId, Long pid);

    Boolean addCollection(Long userId, Long pid);

    Boolean removeCollection(Long userId, Long pid);
}
