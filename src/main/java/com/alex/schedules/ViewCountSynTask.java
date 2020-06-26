package com.alex.schedules;

import com.alex.common.Constant;
import com.alex.entity.Post;
import com.alex.service.PostService;
import com.alex.util.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author wsh
 * @date 2020-06-26
 * 定时同步redis和数据库的中文章浏览值
 */
@Component
public class ViewCountSynTask {
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PostService postService;

    @Scheduled(cron = "0 0/1 * * * *") //每分钟同步一次
    public void task(){
        Set<String> postKeys = redisTemplate.keys(Constant.RANK_POST_KEY_PREFIX + "*");
        List<String> ids = new ArrayList<>();
        for (String postKey : postKeys) {
            if(redisUtil.hHasKey(postKey, "post:viewCount")){
                //需要同步更新的文章id
                ids.add(postKey.substring(Constant.RANK_POST_KEY_PREFIX.length()));
            }
        }
        if(ids.isEmpty()){
            return;
        }
        //需要更新阅读量的文章
        List<Post> posts = postService.list(new QueryWrapper<Post>().in("id", ids));
        posts.stream().forEach((post) -> {
            //获取redis中文章的阅读量
            Integer viewCount = (Integer) redisUtil.hget(
                    Constant.RANK_POST_KEY_PREFIX + post.getId(), "post:viewCount");
            post.setViewCount(viewCount);
        });
        if(posts.isEmpty()){
            return;
        }
        //同步更新数据库
        boolean flag = postService.updateBatchById(posts);
        //如果成功，将redis中缓存的阅读量删除
        if(flag){
            ids.stream().forEach((id) -> {
                redisUtil.hdel(Constant.RANK_POST_KEY_PREFIX + id, "post:viewCount");
                System.out.println(id + "同步成功");
            });
        }
    }
}
