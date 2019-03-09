package com.nowcoder.controller;

import com.nowcoder.dao.MessageDAO;
import com.nowcoder.model.*;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.ToutiaoUtil;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author cyx
 * @data 2019/1/27 15:10
 */
@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);


    @Autowired
    HostHolder hostHolder;

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @RequestMapping(path={"/msg/detail"},method = RequestMethod.GET)
    public String conversationDetail(Model model,
                                     @Param("conversationId") String conversationId){
      try {
          //消息列表 条数限制
          List<Message> conversationList  = messageService.getConversationDetail(conversationId,0,10);
          List<ViewObject> messages=new ArrayList<>();
          for(Message msg:conversationList){
            ViewObject vo = new ViewObject();
            vo.set("message",msg);
            User user = userService.getUser(msg.getFromId());//获取发送消息的人
            if(user==null){
                continue;
            }
            vo.set("headUrl",user.getHeadUrl());
            vo.set("userId",user.getId());
            vo.set("username",user.getName());
            messages.add(vo);
          }
          model.addAttribute("messages",messages);
          int num = messageService.getUnreadOnHeader(hostHolder.getUser().getId());
          model.addAttribute("num",num);
      }catch (Exception e){
            logger.error("获取详情消息失败"+e.getMessage());
      }
        return "letterDetail";
    }

    @RequestMapping(path={"/msg/addMessage"},method = RequestMethod.POST)
    @ResponseBody
    public String addMessage(@RequestParam("fromId") int fromId,
                             @RequestParam("toId")int  toId,
                             @RequestParam("content")String content){
        try{
            Message msg= new Message();
            msg.setFromId(fromId);
            msg.setContent(content);
            msg.setToId(toId);
            msg.setCreatedDate(new Date());
            msg.setConversationId(fromId<toId?String.format("%d_%d",fromId,toId):String.format("%d_%d",toId,fromId));
            messageService.addMessage(msg);

            return ToutiaoUtil.getJSONString(msg.getId());

        }catch (Exception e){
            logger.error("增加评论失败"+e.getMessage());
            return ToutiaoUtil.getJSONString(1,"插入评论失败" );

        }


    }



    @RequestMapping(path={"/msg/list"},method = RequestMethod.GET)
    public String conversationList(Model model, @RequestParam(value = "pop", defaultValue = "0") int pop){
        try{
            if(hostHolder.getUser()!=null){
                int num = messageService.getUnreadOnHeader(hostHolder.getUser().getId());
                model.addAttribute("num",num);
                pop=0;
            }
            model.addAttribute("pop", pop);
            int localUserId=hostHolder.getUser().getId();
            List<ViewObject> conversations = new ArrayList<ViewObject>();
            List<Message> conversationList = messageService.getConversationList(localUserId,0,10);
            for(Message msg :conversationList){
                ViewObject vo = new ViewObject();
                vo.set("conversation",msg);
                vo.set("id",msg.getConversationId());
                int targetId = msg.getFromId()==localUserId?msg.getToId():msg.getFromId();
                User user=userService.getUser(targetId);
                vo.set("user",user);
                vo.set("headUrl",user.getHeadUrl());
                vo.set("userId",user.getId());
                vo.set("name",user.getName());
                //vo.set("targetId", targetId);
                //vo.set("totalCount",msg.getId());
                vo.set("unread",messageService.getConversationUnreadCount(localUserId,msg.getConversationId()));
               // vo.set("unreadCount",msg.)
                conversations.add(vo);

            }
            model.addAttribute("conversations",conversations);
        }catch (Exception e){
            logger.error("获取站内信列表失败"+e.getMessage());

        }
        return "letter";
    }


    @RequestMapping(path={"/deleteletter"},method = RequestMethod.POST)
    public String Deleteletter(String deleteid){
        messageService.deleteletter(deleteid);
        return "letter";
    }

    @RequestMapping(path={"/deleteletterdetail"},method = RequestMethod.POST)
    public String deleteletterdetile(int deleteid){
        messageService.deleteMessageDetailById(deleteid);
        return "letterDetail";
    }

    @RequestMapping(path={"/markHasread"},method = RequestMethod.POST)
    public void markhasread(int markid){
        messageService.markHasread(markid);
    }
}
