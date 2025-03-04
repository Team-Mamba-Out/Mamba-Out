package org.mamba.mapper;

import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;

@Mapper
public interface MessageMapper {

    @Insert("INSERT INTO mamba.message (Uid, title, text, createTime, isRead) VALUES (#{Uid}, #{title}, #{text}, #{createTime}, #{isRead})")
    void createMessage(@Param("Uid") Integer Uid,
                       @Param("title") String title,
                       @Param("text") String text,
                       @Param("createTime") LocalDateTime createTime,
                       @Param("isRead") boolean isRead);

    @Delete("DELETE FROM mamba.message WHERE id = #{id}")
    void deleteMessage(@Param("id") Integer id);
}