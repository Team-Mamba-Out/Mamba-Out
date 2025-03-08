package org.mamba.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.mamba.entity.Message;
import org.mamba.entity.Record;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
     * @param sender the sender of the message
     */
    @Insert("INSERT INTO mamba.message (Uid, title, text, createTime, isRead, sender) VALUES (#{Uid}, #{title}, #{text}, #{createTime}, #{isRead}, #{sender})")
    void createMessage(@Param("Uid") Integer Uid,
                       @Param("title") String title,
                       @Param("text") String text,
                       @Param("createTime") LocalDateTime createTime,
                       @Param("isRead") boolean isRead,
                       @Param("sender") String sender);

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

    /**
     * counts the total number of messages (or those satisfying the conditions)
     */
    @SelectProvider(type = MessageMapper.MessageSqlBuilder.class, method = "buildCountMessagesSql")
    Integer count(@Param("Uid") Integer Uid,
                  @Param("title") String title,
                  @Param("text") String text,
                  @Param("createTime") LocalDateTime createTime,
                  @Param("isRead") Boolean isRead,
                  @Param("sender") String sender);

    /**
     * Obtains the list of records specified by the start time.
     */
    @Select("SELECT * FROM mamba.record WHERE startTime = #{startTime}")
    List<Record> getRecordsByStartTime(@Param("startTime") LocalDateTime startTime);

    /**
     * Static class for building SQL queries.
     */
    class MessageSqlBuilder {
        public static String buildCountMessagesSql(Map<String, Object> params) {
//            @Param("Uid") Integer Uid,
//            @Param("title") String title,
//            @Param("text") String text,
//            @Param("createTime") LocalDateTime createTime,
//            @Param("isRead") boolean isRead,
//            @Param("sender") String sender
            SQL sql = new SQL() {{
                SELECT("count(*)");
                FROM("mamba.message");

                if (params.get("uid") != null) {
                    WHERE("uid = #{uid}");
                }
                if (params.get("title") != null && !params.get("title").toString().isEmpty()) {
                    WHERE("title = #{title}");
                }
                if (params.get("text") != null && !params.get("text").toString().isEmpty()) {
                    WHERE("text = #{text}");
                }
                if (params.get("createTime") != null) {
                    WHERE("createTime = #{createTime}");
                }
                if (params.get("isRead") != null) {
                    WHERE("isRead = #{isRead}");
                }
                if (params.get("sender") != null && !params.get("sender").toString().isEmpty()) {
                    WHERE("sender = #{sender}");
                }
            }};

            return sql.toString();
        }
    }
}