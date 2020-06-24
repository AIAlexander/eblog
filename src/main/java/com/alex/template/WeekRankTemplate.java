package com.alex.template;

import com.alex.common.templates.DirectiveHandler;
import com.alex.common.templates.TemplateDirective;
import com.alex.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author wsh
 * @date 2020-06-24
 * 本周热议freemarker组件
 */
@Component
public class WeekRankTemplate extends TemplateDirective {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public String getName() {
        return "rank";
    }

    @Override
    public void execute(DirectiveHandler handler) throws Exception {
        List<Map> rankMaps = new ArrayList<>();
        String key = "week:rank";
        String postKey = "rank:post:";
        //redis中获取"week:rank"的值
        Set<ZSetOperations.TypedTuple> rankSet = redisUtil.getZSetRank(key, 0, 6);
        for (ZSetOperations.TypedTuple rank : rankSet) {
            Map<String, Object> map = new HashMap<>();
            Object postId = rank.getValue();
            //post的id
            map.put("id", postId);
            //获取"rank:post:id"的title值
            map.put("title", redisUtil.hget(postKey + postId, "post:title"));
            map.put("commentCount", rank.getScore());
            rankMaps.add(map);
        }
        handler.put(RESULTS, rankMaps).render();
    }
}
