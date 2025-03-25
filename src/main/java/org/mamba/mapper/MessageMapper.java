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
     * @param receiver the user ID
     * @param title the title of the message
     * @param text the text of the message
     * @param createTime the creation time of the message
     * @param isRead the read status of the message
     * @param sender the sender of the message
     */
    @Insert("INSERT INTO mamba.message (receiver, title, text, createTime, isRead, sender, type, roomId) VALUES (#{receiver}, #{title}, #{text}, now(), #{isRead}, #{sender}, #{type}, #{roomId})")
    void createMessage(@Param("receiver") Integer receiver,
                       @Param("title") String title,
                       @Param("text") String text,
                       @Param("createTime") LocalDateTime createTime,
                       @Param("isRead") boolean isRead,
                       @Param("sender") String sender,
                       @Param("type") Integer type,
                       @Param("roomId") Integer roomId);

    @Select("SELECT COUNT(*) from mamba.message where receiver = #{receiver} and isRead = FALSE")
    int countUnreadMessage(@Param("receiver") Integer receiver);

    /**
     * Deletes a message from the database by its ID.
     *
     * @param id the ID of the message to delete
     */
    @Delete("DELETE FROM mamba.message WHERE id = #{id}")
    void deleteMessage(@Param("id") Integer id);

    /**
     * Retrieves a paginated list of messages for a given user ID.
     */
    @SelectProvider(type = MessageMapper.MessageSqlBuilder.class, method = "buildGetMessagesSql")
    List<Message>getMessagesBySender(@Param("sender") Integer sender,
                                     @Param("receiver") Integer receiver,
                                     @Param("pageSize") Integer pageSize,
                                     @Param("offset") Integer offset);

    /**
     * Retrieves a paginated list of messages for a given user ID.
     */
    @SelectProvider(type = MessageMapper.MessageSqlBuilder.class, method = "buildGetMessagesSql")
    List<Message> getMessagesByReceiver(@Param("receiver") Integer receiver,
                                        @Param("sender") Integer sender,
                                   @Param("pageSize") Integer pageSize,
                                   @Param("offset") Integer offset);

    /**
     * Retrieves the total count of messages for a given user ID.
     *
     * @param receiver the user ID
     * @return the total number of messages for the specified user
     */
    @Select("SELECT COUNT(*) FROM mamba.message WHERE receiver = #{receiver}")
    int getMessagesCountByUid(@Param("receiver") Integer receiver);
    /**
     * Retrieves the total count of messages for a given user ID.
     *
     * @param sender the user ID
     * @return the total number of messages for the specified user
     */
    @Select("SELECT COUNT(*) from mamba.message where sender like CONCAT(#{sender}, '%')")
    int getMessagesCountBySender(@Param("sender") Integer sender);

    /**
     * counts the total number of messages (or those satisfying the conditions)
     */
    @SelectProvider(type = MessageMapper.MessageSqlBuilder.class, method = "buildCountMessagesSql")
    Integer count(@Param("receiver") Integer receiver,
                  @Param("title") String title,
                  @Param("text") String text,
                  @Param("createTime") LocalDateTime createTime,
                  @Param("isRead") Boolean isRead,
                  @Param("sender") String sender,
                  @Param("type") Integer type,
                  @Param("roomId") Integer roomId);

    /**
     * Obtains the list of records specified by the start time.
     */
    @Select("SELECT * FROM mamba.record WHERE startTime = #{startTime}")
    List<Record> getRecordsByStartTime(@Param("startTime") LocalDateTime startTime);

    /**
     * if the user reads the message, modify its status
     */
    @Update("update message set isRead=TRUE where id = #{id}")
    void updateIsRead(@Param("id") Integer id);

    @Select("SELECT * FROM mamba.message WHERE receiver = #{receiver} AND createTime > #{lastTimestamp} ORDER BY createTime ASC")
    List<Message> getMessagesAfter(@Param("receiver") Integer receiver, @Param("lastTimestamp") LocalDateTime lastTimestamp);

    /**
     * Static class for building SQL queries.
     */
    class MessageSqlBuilder {
        public static String buildGetMessagesSql(Map<String, Object> params) {
            SQL sql = new SQL() {{
                SELECT("*");
                FROM("mamba.message");
                if (params.get("receiver") != null) {
                    WHERE("receiver = #{receiver}");
                }
                if (params.get("sender") != null) {
                    WHERE("sender like CONCAT(#{sender}, '%')");
                }
            }};
            // Deal with LIMIT and OFFSET
            String query = sql.toString();
            if (params.get("pageSize") != null) {
                if (params.get("offset") != null) {
                    query += " LIMIT #{pageSize} OFFSET #{offset}";
                } else {
                    query += " LIMIT #{pageSize}";
                }
            }
            return query;
        }

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

                if (params.get("receiver") != null) {
                    WHERE("receiver = #{receiver}");
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
                if (params.get("type") != null) {
                    WHERE("type = #{type}");
                }
                if (params.get("roomId") != null) {
                    WHERE("roomId = #{roomId}");
                }
            }};

            return sql.toString();
        }
    }
}