package com.nowcoder.async;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author cyx
 * @data 2019/2/16 15:09
 */
@Service
public class EventProducer {
    @Autowired
    JedisAdapter jedisAdapter;

    public boolean fireEvent(EventModel model){
        String json = JSONObject.toJSONString(model);
        String key = RedisKeyUtil.getEventQueueKey();
        jedisAdapter.lpush(key,json);
        return true;
    }

}
