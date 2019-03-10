package com.toutiaozixun.controller;

import com.toutiaozixun.async.EventModel;
import com.toutiaozixun.async.EventProducer;
import com.toutiaozixun.async.EventType;
import com.toutiaozixun.model.EntityType;
import com.toutiaozixun.model.HostHolder;
import com.toutiaozixun.model.News;
import com.toutiaozixun.service.LikeService;
import com.toutiaozixun.service.NewsService;
import com.toutiaozixun.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
