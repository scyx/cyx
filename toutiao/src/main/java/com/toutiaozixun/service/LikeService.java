package com.toutiaozixun.service;

import com.toutiaozixun.util.JedisAdapter;
import com.toutiaozixun.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author cyx
 * @data 2019/2/6 10:19
 * 如果喜欢返回1 如果不喜欢返回-1 否则返回0
 */
@Service
public class LikeService {
    @Autowired
    JedisAdapter jedisAdapter;

    public int getLikeStatus(int userId,int entityType,int entityId){
        String likeKey= RedisKeyUtil.getLikeKey(entityId,entityType);
        if(jedisAdapter.sismember(likeKey,String.valueOf(userId))){
            return 1;
        }
        String disLikeKey=RedisKeyUtil.getDisLikeKey(entityId,entityType);
        return jedisAdapter.sismember(disLikeKey,String.valueOf(userId))?-1:0;
    }

    public void cancelLike(int userId,int entityType,int entityId){
        String likeKey=RedisKeyUtil.getLikeKey(entityId,entityType);
        jedisAdapter.srem(likeKey,String.valueOf(userId));
    }

    public void cancelDisLike(int userId,int entityType,int entityId){
        String dislikeKey=RedisKeyUtil.getDisLikeKey(entityId,entityType);
        jedisAdapter.srem(dislikeKey,String.valueOf(userId));
    }


    public long like(int userId,int entityType,int entityId){
        String likeKey=RedisKeyUtil.getLikeKey(entityId,entityType);
        jedisAdapter.sadd(likeKey,String.valueOf(userId));
        String disLikeKey= RedisKeyUtil.getDisLikeKey(entityId,entityType);
        jedisAdapter.srem(disLikeKey,String.valueOf(userId));
        return jedisAdapter.scard(likeKey);
    }

    public long getNumberoflike(int entityType,int entityId){
        String likeKey=RedisKeyUtil.getLikeKey(entityId,entityType);
        return jedisAdapter.scard(likeKey);
    }

    public long getNumberofDislike(int entityType,int entytyId ){
        String dislikekey = RedisKeyUtil.getDisLikeKey(entytyId,entityType);
        return jedisAdapter.scard(dislikekey);
    }


    public long disLike(int userId,int entityType,int entityId){
        String disLikeKey=RedisKeyUtil.getDisLikeKey(entityId,entityType);
        jedisAdapter.sadd(disLikeKey,String.valueOf(userId));
        String likeKey= RedisKeyUtil.getLikeKey(entityId,entityType);
        jedisAdapter.srem(likeKey,String.valueOf(userId));
        return jedisAdapter.scard(likeKey);
    }
}
