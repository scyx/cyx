package com.toutiaozixun.async;

import com.alibaba.fastjson.JSON;
import com.toutiaozixun.util.JedisAdapter;
import com.toutiaozixun.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author cyx
 * @data 2019/2/16 15:17
 */
@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {
    private static final Logger logger= LoggerFactory.getLogger(EventConsumer.class);
    private Map<EventType, List<EventHandler>> config=new HashMap<EventType, List<EventHandler>>();
    private ApplicationContext applicationContext;
    @Autowired
    JedisAdapter jedisAdapter;

    @Override
    //项目启动的时候要注册所有的事件类型 然后来判断一个事件是否能被处理
    public void afterPropertiesSet() throws Exception {
        Map<String,EventHandler> beans=applicationContext.getBeansOfType(EventHandler.class);
        if(beans!=null){
            for(Map.Entry<String,EventHandler> entry:beans.entrySet()){
                List<EventType> eventTypes = entry.getValue().getSupportEventType();
                for(EventType type:eventTypes){
                    if(!config.containsKey(type)){
                        config.put(type,new ArrayList<EventHandler>());
                    }
                    config.get(type).add(entry.getValue());
                }
            }
        }
        //开个线程来不断处理队列中的事件
        Thread thread = new Thread(new Runnable() {
            public void run(){
                while(true){
                    String key= RedisKeyUtil.getEventQueueKey();
                    List<String> events = jedisAdapter.brpop(0,key);
                    for(String message:events){
                        if(message.equals(key)) {
                            continue;
                        }
                        EventModel eventModel= JSON.parseObject(message,EventModel.class);
                        if(!config.containsKey(eventModel.getType())){
                            logger.error("不能处理的事件" );
                            continue;
                        }
                        for(EventHandler handler:config.get(eventModel.getType())){
                           handler.doHandle(eventModel);
                        }
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
}
