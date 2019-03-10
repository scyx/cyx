package com.toutiaozixun.async;

import java.util.List;

/**
 * @author cyx
 * @data 2019/2/16 15:04
 */
public interface EventHandler {

    void doHandle(EventModel model);//每个handler对事情的处理不一样 抽象为接口
    List<EventType> getSupportEventType();//关注哪些eventtype 只要发生了这些EventType都要处理
}
