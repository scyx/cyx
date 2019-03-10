package com.toutiaozixun.controller;

import com.toutiaozixun.model.EntityType;
import com.toutiaozixun.model.HostHolder;
import com.toutiaozixun.model.News;
import com.toutiaozixun.model.ViewObject;
import com.toutiaozixun.service.LikeService;
import com.toutiaozixun.service.MessageService;
import com.toutiaozixun.service.NewsService;
import com.toutiaozixun.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@Controller
public class HomeController {
    @Autowired
    NewsService newsService;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    LikeService likeService;

    @Autowired
    MessageService messageService;

    private List<ViewObject> getNews(int userId, int offset, int limit) {
        List<News> newsList = newsService.getLatestNews(userId, offset, limit);
        List<ViewObject> vos = new ArrayList<>();
        int localUserId=hostHolder.getUser()!=null?hostHolder.getUser().getId(): 0;
        for (News news : newsList) {
            ViewObject vo = new ViewObject();
            vo.set("news", news);
            vo.set("user", userService.getUser(news.getUserId()));
            if(localUserId!=0){
               vo.set("like", likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS,news.getId()));
            }else{
                vo.set("like",0);
            }
            vos.add(vo);
        }
        return vos;
    }

    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model,
                        @RequestParam(value = "pop", defaultValue = "0") int pop) {
        model.addAttribute("vos", getNews(0, 0, 20));
        if(hostHolder.getUser()!=null){
            int num = messageService.getUnreadOnHeader(hostHolder.getUser().getId());
            model.addAttribute("num",num);
            pop=0;
        }
        model.addAttribute("pop", pop);
        return "home";
    }

    @RequestMapping(path = {"/user/{userId}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userIndex(Model model, @PathVariable("userId") int userId,
                            @RequestParam(value = "pop", defaultValue = "0") int pop) {
        model.addAttribute("vos", getNews(userId, 0, 10));
        if(hostHolder.getUser()!=null){
            int num = messageService.getUnreadOnHeader(hostHolder.getUser().getId());
            model.addAttribute("num",num);
            pop=0;
        }
        model.addAttribute("pop", pop);
        return "home";
    }


}
