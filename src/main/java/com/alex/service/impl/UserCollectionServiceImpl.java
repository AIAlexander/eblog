package com.alex.service.impl;

import com.alex.entity.Post;
import com.alex.entity.UserCollection;
import com.alex.mapper.UserCollectionMapper;
import com.alex.service.PostService;
import com.alex.service.UserCollectionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author alex
 * @since 2020-06-15
 */
@Service
public class UserCollectionServiceImpl extends ServiceImpl<UserCollectionMapper, UserCollection> implements UserCollectionService {

    @Autowired
    private PostService postService;


    @Override
    public Integer getCollectionNumByPostId(Long userId, Long pid) {
        return this.count(new QueryWrapper<UserCollection>()
                .eq("user_id", userId)
                .eq("post_id", pid)
        );
    }

    @Override
    public Boolean addCollection(Long userId, Long pid) {
        //先判断是否有文章
        int postNum = postService.getPostNumByPostId(pid);
        if(postNum > 0){
            //判断用户是否收藏文章
            Integer collectionNumByPostId = getCollectionNumByPostId(userId, pid);
            if(collectionNumByPostId != null && collectionNumByPostId == 0){
                UserCollection userCollection = new UserCollection();
                userCollection.setPostId(pid);
                userCollection.setUserId(userId);
                userCollection.setPostUserId(userId);
                userCollection.setCreated(new Date());
                userCollection.setModified(new Date());
                return this.save(userCollection);
            }
        }
        return false;
    }

    @Override
    public Boolean removeCollection(Long userId, Long pid) {
        Post post = postService.getById(pid);
        if(post == null){
            return false;
        }

        return this.remove(new QueryWrapper<UserCollection>()
                .eq("user_id", userId)
                .eq("post_id", pid)
        );
    }
}
