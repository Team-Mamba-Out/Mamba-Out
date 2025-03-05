package org.mamba.service.impl;

import org.mamba.mapper.MessageMapper;
import org.mamba.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.mamba.entity.Message;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of the MessageService interface.
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    /**
     * Creates a new message.
     *
     * @param Uid the user ID
     * @param title the title of the message
     * @param text the text of the message
     * @param createTime the creation time of the message
     * @param isRead the read status of the message
     */
    @Override
    public void createMessage(Integer Uid, String title, String text, LocalDateTime createTime, Boolean isRead) {
        messageMapper.createMessage(Uid, title, text, createTime, isRead);
    }

    /**
     * Deletes a message by its ID.
     *
     * @param id the ID of the message to delete
     */
    @Override
    public void deleteMessage(Integer id) {
        messageMapper.deleteMessage(id);
    }

    /**
     * Retrieves all messages for a given user ID.
     *
     * @param Uid the user ID
     * @return a list of messages
     */
    @Override
    public List<Message> getMessagesByUid(Integer Uid) {
        return messageMapper.getMessagesByUid(Uid);
    }
}