package com.toutiaozixun.dao;

import com.toutiaozixun.model.News;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface NewsDAO {
    String TABLE_NAME = "news";
    String INSERT_FIELDS = " title, link, image, like_count, comment_count, created_date, user_id ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{title},#{link},#{image},#{likeCount},#{commentCount},#{createdDate},#{userId})"})
    int addNews(News news);


    List<News> selectByUserIdAndOffset(@Param("userId") int userId, @Param("offset") int offset,
                                       @Param("limit") int limit);



    @Select({"select ", SELECT_FIELDS , " from ", TABLE_NAME, " where id=#{id}"})
    News getById(int id);

    @Update({"update ", TABLE_NAME, " set comment_count = #{commentCount} where id=#{id}"})
    int updateCommentCount(@Param("id") int id, @Param("commentCount") int commentCount);

    @Update({"update",TABLE_NAME,"set like_count=#{likeCount} where id = #{id}"})
    int updateLikeCount(@Param("id")int id,@Param("likeCount")int likeCount);

    @Select({"select user_id from",TABLE_NAME,"where id= #{newsId}"})
    int selectuserIdBynewsId(@Param("newsId")int newsId);
}
