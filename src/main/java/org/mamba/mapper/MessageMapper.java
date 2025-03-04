package org.mamba.mapper;

import org.apache.ibatis.annotations.*;
import org.mamba.entity.Message;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Mapper interface for database operations on the Message entity.
 */
@Mapper
public interface MessageMapper {

    /**
     * Inserts a new message into the database.
     *
     * @param Uid the user ID
     * @param title the title of the message
     * @param text the text of the message
     * @param createTime the creation time of the message
     * @param isRead the read status of the message
     */
    @Insert("INSERT INTO mamba.message (Uid, title, text, createTime, isRead) VALUES (#{Uid}, #{title}, #{text}, #{createTime}, #{isRead})")
    void createMessage(@Param("Uid") Integer Uid,
                       @Param("title") String title,
                       @Param("text") String text,
                       @Param("createTime") LocalDateTime createTime,
                       @Param("isRead") boolean isRead);

    /**
     * Deletes a message from the database by its ID.
     *
     * @param id the ID of the message to delete
     */
    @Delete("DELETE FROM mamba.message WHERE id = #{id}")
    void deleteMessage(@Param("id") Integer id);

    /**
     * Retrieves all messages for a given user ID.
     *
     * @param Uid the user ID
     * @return a list of messages
     */
    @Select("SELECT * FROM mamba.message WHERE Uid = #{Uid}")
    List<Message> getMessagesByUid(@Param("Uid") Integer Uid);
}