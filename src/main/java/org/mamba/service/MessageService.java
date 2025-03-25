package org.mamba.service;

import org.mamba.entity.Message;
import org.mamba.entity.Record;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Service interface for managing messages.
 */
public interface MessageService {
    /**
     * Creates a new message.
     *
     * @param receiver the user ID
     * @param title the title of the message
     * @param text the text of the message
     * @param createTime the creation time of the message
     * @param isRead the read status of the message
     */
    void createMessage(Integer receiver, String title, String text, LocalDateTime createTime, Boolean isRead,String sender,Integer type,Integer roomId);

    int countUnreadMessage(Integer receiver);
    /**
     * Deletes a message by its ID.
     *
     * @param id the ID of the message to delete
     */
    void deleteMessage(Integer id);

    Map<String,Object> getMessagesBySender(Integer sender, Integer size, Integer page);
    /**
     * Retrieves a paginated list of messages for a given user ID.
     *
     * @param receiver  the user ID
     * @param size the number of messages per page
     * @param page the current page number
     * @return a map containing the list of messages, total pages, total messages, and current page number
     */
    Map<String, Object> getMessagesByReceiver(Integer receiver, Integer size, Integer page);

    /**
     * Obtains the list of records specified by the start time.
     *
     * @param startTime the provided start time
     * @return the corresponding record list
     */
    void Reminder(LocalDateTime startTime);

    /**
     * if the user reads the message, modify its status
     */
    void readMessage(Integer id);

    /**
     * Retrieve messages for a specific user after the last retrieved timestamp.
     *
     * @param receiver the user ID
     * @param lastTimestamp the last retrieved message timestamp
     * @return list of new messages
     */
    List<Message> getMessagesAfter(Integer receiver, LocalDateTime lastTimestamp);
}