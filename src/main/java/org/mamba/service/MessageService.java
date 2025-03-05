package org.mamba.service;

import org.mamba.entity.Message;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for managing messages.
 */
public interface MessageService {
    /**
     * Creates a new message.
     *
     * @param Uid the user ID
     * @param title the title of the message
     * @param text the text of the message
     * @param createTime the creation time of the message
     * @param isRead the read status of the message
     */
    void createMessage(Integer Uid, String title, String text, LocalDateTime createTime, Boolean isRead);

    /**
     * Deletes a message by its ID.
     *
     * @param id the ID of the message to delete
     */
    void deleteMessage(Integer id);

    /**
     * Retrieves all messages for a given user ID.
     *
     * @param Uid the user ID
     * @return a list of messages
     */
    List<Message> getMessagesByUid(Integer Uid);
}