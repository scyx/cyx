package com.toutiaozixun.service;

import com.toutiaozixun.dao.MessageDAO;
import com.toutiaozixun.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author cyx
 * @data 2019/1/27 15:24
 */
@Service
public class MessageService {
    @Autowired
    MessageDAO messageDAO;

    public int addMessage(Message message){
        return  messageDAO.addMessage(message);
    }



    public List<Message> getConversationDetail(String conversationId,int offset,int limit){
        return messageDAO.getConversationDetail(conversationId,offset,limit);
    }
    public void deleteletter(String conversastionId){
        messageDAO.deleteMessage(conversastionId);
    }


    public List<Message> getConversationList(int UserId,int offset,int limit){
        return messageDAO.getConversationList(UserId,offset,limit);
    }
     public int getConversationUnreadCount(int userId, String conversationId){
        return messageDAO.getConversationUnreadCount(userId,conversationId);
     }

     public void deleteMessageDetailById(int id){
        messageDAO.deleteMessageDetailById(id);
     }

     public void markHasread(int markid){
        messageDAO.markHasread(markid);
     }

     public int getUnreadOnHeader(int userId){
        return messageDAO.getUnreadOnheader(userId);
     }
}
