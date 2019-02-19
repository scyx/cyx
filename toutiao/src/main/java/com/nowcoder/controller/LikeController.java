package com.nowcoder.controller;

import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.service.LikeService;
import com.nowcoder.service.NewsService;
import com.nowcoder.util.ToutiaoUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author cyx
 * @data 2019/2/6 10:32
 */
@Controller
public class LikeController {
    @Autowired
    HostHolder hostHolder;

    @Autowired
    LikeService likeService;

    @Autowired
    NewsService newsService;

    @RequestMapping(path = {"/like"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String like(@RequestParam("newsId") int newsId) {
        int userId = hostHolder.getUser().getId();
        long likecount =likeService.like(userId, EntityType.ENTITY_NEWS,newsId);
        newsService.updateLikeCount(newsId,(int)likecount);
        return ToutiaoUtil.getJSONString(0,String.valueOf(likecount));
    }


    @RequestMapping(path = {"/dislike"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String dislike(@RequestParam("newsId") int newsId) {
        int userId = hostHolder.getUser().getId();
        long dislikecount =likeService.disLike(userId, EntityType.ENTITY_NEWS,newsId);
        newsService.updateLikeCount(newsId,(int)dislikecount);
        return ToutiaoUtil.getJSONString(0,String.valueOf(dislikecount));
    }
}
