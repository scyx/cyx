package com.toutiaozixun.service;

import com.toutiaozixun.dao.CommentDAO;
import com.toutiaozixun.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author cyx
 * @data 2019/1/24 15:10
 */
@Service
public class CommentService {

    @Autowired
    private CommentDAO commentDAO;

    public List<Comment> getCommentsByEntity(int entityId, int entityType){
        return commentDAO.selectByEntity(entityId,entityType);
    }
    public int addComment(Comment comment){
        return commentDAO.addComment(comment);

    }
    public int getCommentCount(int entityId,int entityType){
        return commentDAO.getCommentCount(entityId,entityType);
    }

}
