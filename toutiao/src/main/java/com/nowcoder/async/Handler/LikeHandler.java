package com.nowcoder.async.Handler;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.NewsService;
import com.nowcoder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author cyx
 * @data 2019/2/16 15:07
 */
@Component
public class LikeHandler implements EventHandler {
    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Autowired
    NewsService newsService;

    @Override
    public void doHandle(EventModel model) {
        Message message = new Message();
        int toId=model.getEntityOwnerId();
        int fromId=16;
        message.setFromId(fromId);
        message.setToId(toId);
        message.setConversationId(fromId<toId?String.format("%d_%d",fromId,toId):String.format("%d_%d",toId,fromId));
        User user=userService.getUser(model.getActorId());
        message.setContent("用户"+"<a href=/user/"+user.getId()+">"+user.getName()+"</a>"+"赞了你的资讯"+"<a href="+"\""+"/news/"+newsService.getById(model.getEntityId()).getId()+"\""+"\""+" >"+"\""+newsService.getById(model.getEntityId()).getTitle()+"</a>"+"\"");
        message.setCreatedDate(new Date());
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventType() {
        return Arrays.asList(EventType.Like);
    }
}