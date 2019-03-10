package com.toutiaozixun.controller;

import com.toutiaozixun.async.EventModel;
import com.toutiaozixun.async.EventProducer;
import com.toutiaozixun.async.EventType;
import com.nowcoder.model.*;
import com.nowcoder.service.*;
import com.toutiaozixun.model.*;
import com.toutiaozixun.service.*;
import com.toutiaozixun.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author cyx
 * @data 2019/1/23 14:46
 */
@Controller
public class NewsController {
    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);
    @Autowired
    NewsService newsService;
    @Autowired
    AliyunService aliyunService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    CommentService commentService;
    @Autowired
    UserService userService;
    @Autowired
    LikeService likeService;
    @Autowired
    MessageService messageService;
    @Autowired
    EventProducer eventProducer;


    @RequestMapping(path={"/addComment"},method = RequestMethod.POST)
    public String addComment(@RequestParam("newsId") int newsId,
                             @RequestParam("content")String content){
        try{
            content= HtmlUtils.htmlEscape(content);
            Comment comment=new Comment();
            comment.setUserId(hostHolder.getUser().getId());
            comment.setContent(content);
            comment.setEntityType(EntityType.ENTITY_NEWS);
            comment.setEntityId(newsId);
            comment.setCreatedDate(new Date());
            comment.setStatus(0);
            commentService.addComment(comment);
            //更新news里面的评论数量
            int count= commentService.getCommentCount(comment.getEntityId(),comment.getEntityType());
            newsService.updateCommentCount(comment.getEntityId(),count);
            int userId= newsService.selectuserIdBynewsId(newsId);
            eventProducer.fireEvent(new EventModel(EventType.COMMENT)
                    .setActorId(hostHolder.getUser().getId())
                    .setEntityOwnerId(userId).setEntityId(newsId));

        }catch (Exception e){
            logger.error("增加评论失败"+e.getMessage());
        }
        return "redirect:/news/"+String.valueOf(newsId);
    }



    @RequestMapping(path = {"/image"}, method = { RequestMethod.GET})
    @ResponseBody
    public void getImage(@RequestParam ("name")String imagename,
                        HttpServletResponse response){
        try{
            response.setContentType("image/jpeg");
            StreamUtils .copy(new FileInputStream(new File(
                ToutiaoUtil.IMAGE_DIR+imagename)),response.getOutputStream());
    }catch(Exception e){
            logger.error("读取图片错误"+e.getMessage());
    }

    }

    //上传图片 采用的是阿里云服务
    @RequestMapping(path = {"/uploadImage"}, method = { RequestMethod.POST})
    @ResponseBody
    public String uploadImage(@RequestParam("file")MultipartFile file){
        try{
            String fileUrl= aliyunService.saveImage(file);
            if(fileUrl==null){
                return ToutiaoUtil.getJSONString(1,"上传图片失败");
            }
            return ToutiaoUtil.getJSONString(0,fileUrl);
        }catch (Exception e){
            logger.error("上传图片失败"+e.getMessage());
            return ToutiaoUtil.getJSONString(1,"上传失败");
        }

    }
    //添加新闻
    @RequestMapping(path={"/user/addNews/"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addNews(@RequestParam ("image")String image,
                          @RequestParam ("title")String title,
                          @RequestParam("link")String link){
        try{
            News news = new News();
            if(hostHolder.getUser()!=null){
                news.setUserId(hostHolder.getUser().getId());
            }else{
                news.setUserId(3);
            }
            news.setImage(image);
            news.setCreatedDate(new Date());
            news.setTitle(title);
            news.setLink(link);
            newsService.addNews(news);
            return ToutiaoUtil.getJSONString(0);

        }catch (Exception e){
            logger.error("添加咨询错误"+e.getMessage());
            return ToutiaoUtil.getJSONString(1,"发布失败");
        }
    }

    //新闻详情页
    @RequestMapping(path={"news/{newsId}"},method = RequestMethod.GET)
    public String newsDetail(@PathVariable("newsId")int newsId, Model model){
        News news= newsService.getById(newsId);
        int localUserId=hostHolder.getUser()!=null?hostHolder.getUser().getId():0;
        if(news!=null){
            //评论
            List<Comment> comments=commentService.getCommentsByEntity(news.getId(), EntityType.ENTITY_NEWS);
            List<ViewObject> commentVOS = new ArrayList<ViewObject>();
            for(Comment comment :comments){
                ViewObject vo = new ViewObject();
                vo.set("comment",comment);
                vo.set("user",userService.getUser(comment.getUserId()) );
                commentVOS.add(vo);
            }

            model.addAttribute("comments",commentVOS);
        }
        if(localUserId!=0){
            model.addAttribute("like", likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS,news.getId()));
            int num = messageService.getUnreadOnHeader(hostHolder.getUser().getId());
            model.addAttribute("num",num);
        }else{
            model.addAttribute("like",0);
        }
        model.addAttribute("news",news);
        model.addAttribute("owner", userService.getUser(news.getUserId()));
        return "detail" ;
    }
}
