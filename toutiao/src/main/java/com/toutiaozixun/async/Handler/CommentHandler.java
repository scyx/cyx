package com.toutiaozixun.async.Handler;

import com.toutiaozixun.async.EventHandler;
import com.toutiaozixun.async.EventModel;
import com.toutiaozixun.async.EventType;
import com.toutiaozixun.model.Message;
import com.toutiaozixun.model.User;
import com.toutiaozixun.service.MessageService;
import com.toutiaozixun.service.NewsService;
import com.toutiaozixun.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author cyx
 * @data 2019/3/4 19:02
 */
@Component
public class CommentHandler implements EventHandler {
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;
    @Autowired
    NewsService newsService;


    @Override
    public void doHandle(EventModel model) {
        Message message = new Message();
        int toId = model.getEntityOwnerId();
        message.setToId(toId);
        int fromId=16;
        message.setCreatedDate(new Date());
        User user  = userService.selectByid(model.getActorId());
        message.setFromId(16);
        message.setContent("用户"+"<a href=/user/"+user.getId()+">"+user.getName()+"</a>"+"评论了你的资讯"+"<a href="+"\""+"/news/"+newsService.getById(model.getEntityId()).getId()+"\""+"\""+" >"+"\""+newsService.getById(model.getEntityId()).getTitle()+"</a>"+"\"");
        message.setConversationId(fromId<toId?String.format("%d_%d",fromId,toId):String.format("%d_%d",toId,fromId));
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventType() {
        return Arrays.asList(EventType.COMMENT);
    }
}
