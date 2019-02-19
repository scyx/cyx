package com.nowcoder.service;

import com.nowcoder.dao.NewsDAO;
import com.nowcoder.model.News;
import com.nowcoder.util.ToutiaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;


@Service
public class NewsService {
    @Autowired
    private NewsDAO newsDAO;

    public List<News> getLatestNews(int userId, int offset, int limit) {
        return newsDAO.selectByUserIdAndOffset(userId, offset, limit);
    }
    public int addNews(News news){
        newsDAO.addNews(news);
        return news.getId();
    }
    public News getById(int newsid){
        return newsDAO.getById(newsid);
    }

    public int updateCommentCount(int id,int count){
        return newsDAO.updateCommentCount(id,count);
    }
    public String saveImage(MultipartFile file) throws  Exception{
        //xxx.jpg
       int doPos =file.getOriginalFilename().lastIndexOf(".");
        if(doPos<0){
            return null;
        }
        String fileExt=file.getOriginalFilename().substring(doPos+1).toLowerCase();
        if(!ToutiaoUtil.isFileAllowed(fileExt)){
            return null;
        }
        String fileName= UUID.randomUUID().toString().replaceAll("-","")+"."+fileExt;
        Files.copy(file.getInputStream(),new File(ToutiaoUtil.IMAGE_DIR+fileName).toPath(),
                StandardCopyOption.REPLACE_EXISTING);
        return ToutiaoUtil.TOUTIAO_DOMAIN+"image?name="+fileName;
    }

    public int updateLikeCount(int id,int likeCount){
        return newsDAO.updateLikeCount(id,likeCount);
    }
}
