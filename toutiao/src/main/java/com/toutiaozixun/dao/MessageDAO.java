package com.toutiaozixun.dao;

import com.toutiaozixun.model.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author cyx
 * @data 2019/1/27 15:08
 */
@Mapper
public interface MessageDAO {
    String TABLE_NAME = "message";
    String INSERT_FIELDS = " from_id, to_id, content, created_date,has_read,conversation_id ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{fromId},#{toId},#{content},#{ createdDate},#{hasRead},#{conversationId})"})
    int addMessage(Message message);

    @Select({"select  ", SELECT_FIELDS,"from",TABLE_NAME,"where conversation_id=#{conversationId} order by id desc limit #{offset},#{limit}"})
    List<Message> getConversationDetail(@Param("conversationId")String conversationId,
                                        @Param("offset")int offset,
                                        @Param("limit")int limit);
    @Select({"select",INSERT_FIELDS,",count(id) as id from (select * from",TABLE_NAME,"where from_id=#{userId} or to_id=#{userId} order by id desc) tt group by conversation_id order by id desc limit #{offset},#{limit} "})
    List<Message> getConversationList(@Param("userId")int userId,
                                        @Param("offset")int offset,
                                        @Param("limit")int limit);

    @Select({"select count(id) from",TABLE_NAME,"where has_read=0 and to_id=#{userId} and conversation_id=#{conversationId}"})
    int getConversationUnreadCount(@Param("userId")int userId,
                                   @Param("conversationId")String conversationId);

    @Delete({"delete from",TABLE_NAME,"where conversation_id=#{conversationId}"})
    void deleteMessage(@Param("conversationId")String conversationId );

    @Delete({"delete from",TABLE_NAME,"where id=#{id}"})
    void deleteMessageDetailById(@Param("id")int id);

    @Update({"update",TABLE_NAME,"set has_read='1' where id=#{id}"})
    void markHasread(@Param("id")int id);


    @Select({"select count(id) from",TABLE_NAME,"where has_read=0 and to_id=#{userId}"})
    int getUnreadOnheader(@Param("userId")int userId );

}
