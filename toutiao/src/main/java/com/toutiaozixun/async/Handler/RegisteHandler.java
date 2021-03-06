package com.toutiaozixun.async.Handler;

import com.toutiaozixun.async.EventHandler;
import com.toutiaozixun.async.EventModel;
import com.toutiaozixun.async.EventType;
import com.toutiaozixun.model.Message;
import com.toutiaozixun.service.MessageService;
import com.toutiaozixun.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author cyx
 * @data 2019/3/3 12:59
 */
@Component
public class RegisteHandler implements EventHandler {
    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;


    @Override
    public void doHandle(EventModel model) {
        Message message = new Message();
        int toId= model.getActorId();
        int fromId = 16;
        message.setToId(toId);
        message.setCreatedDate(new Date());
        message.setContent("欢迎来到今日头条");
        message.setFromId(16);
        message.setConversationId(fromId<toId?String.format("%d_%d",fromId,toId):String.format("%d_%d",toId,fromId));
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventType() {
        return Arrays.asList(EventType.REGEIST);
    }
}
