package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.News;
import com.nowcoder.service.LikeService;
import com.nowcoder.service.NewsService;
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

import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.parser.Entity;
import java.lang.reflect.Method;

/**
 * @author cyx
 * @data 2019/2/6 10:32
 */
@Controller
public class LikeController {
    private static final Logger logger = LoggerFactory.getLogger(LikeController.class);
    @Autowired
    HostHolder hostHolder;

    @Autowired
    LikeService likeService;

    @Autowired
    NewsService newsService;


    @Autowired
    EventProducer eventProducer;
    //点赞
    @RequestMapping(path = {"/like"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String like(@RequestParam("newsId") int newsId) {
        int userId = hostHolder.getUser().getId();
        if(likeService.getLikeStatus(userId,EntityType.ENTITY_NEWS,newsId)==1){
            likeService.cancelLike(userId,EntityType.ENTITY_NEWS,newsId);
            long getlikecount=likeService.getNumberoflike(EntityType.ENTITY_NEWS,newsId);
           //logger.info("点赞取消了");
            newsService.updateLikeCount(newsId, (int) getlikecount);
            return ToutiaoUtil.getJSONString(0,String.valueOf(getlikecount));
        }else {

            long likecount = likeService.like(userId, EntityType.ENTITY_NEWS, newsId);
            News news = newsService.getById(newsId);
            newsService.updateLikeCount(newsId, (int) likecount);
            eventProducer.fireEvent(new EventModel(EventType.Like)
                    .setActorId(hostHolder.getUser().getId()).setEntityId(newsId)
                    .setEntityType(EntityType.ENTITY_NEWS).setEntityOwnerId(news.getUserId()));
            //logger.info("又点赞了");
            return ToutiaoUtil.getJSONString(0, String.valueOf(likecount));
        }
    }

    //点踩 注意和点赞的联系 两者冲突
    @RequestMapping(path = {"/dislike"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String dislike(@RequestParam("newsId") int newsId) {
        int userId = hostHolder.getUser().getId();
        if(likeService.getLikeStatus(userId,EntityType.ENTITY_NEWS,newsId)==-1){
            likeService.cancelDisLike(userId,EntityType.ENTITY_NEWS,newsId);
            long getdislikecount = likeService.getNumberofDislike(EntityType.ENTITY_NEWS,newsId);
            return ToutiaoUtil.getJSONString(0, String.valueOf(getdislikecount));
        }else {
            long dislikecount = likeService.disLike(userId, EntityType.ENTITY_NEWS, newsId);
            newsService.updateLikeCount(newsId, (int) dislikecount);
            return ToutiaoUtil.getJSONString(0, String.valueOf(dislikecount));
        }
    }
}
