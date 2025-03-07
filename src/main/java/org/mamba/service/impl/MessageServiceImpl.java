package org.mamba.service.impl;

import org.mamba.entity.Record;
import org.mamba.mapper.MessageMapper;
import org.mamba.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.mamba.entity.Message;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
    public void createMessage(Integer Uid, String title, String text, LocalDateTime createTime, Boolean isRead, String sender) {
        messageMapper.createMessage(Uid, title, text, createTime, isRead, sender);
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

    /**
     *  * Obtains the list of records that have a start time equal to the provided start time.
     *
     * @param time the provided time
     */
    @Override
    public void Reminder(LocalDateTime time) {
        // Calculate the actual query time (add 10 minutes)
        LocalDateTime startTime = time.plus(10, ChronoUnit.MINUTES);

        // Query records that match the condition
        List<Record> recordList = messageMapper.getRecordsByStartTime(startTime);

        // Iterate through the record list and store each message directly
        for (Record record : recordList) {
            createMessage(
                    record.getUserId(),
                    "Room Reservation Reminder",
                    "Your reserved room " + record.getRoomId() + " is scheduled to start at " + record.getStartTime() + ". Please arrive on time.",
                    LocalDateTime.now(),  // Message creation time
                    false,  // Default to unread
                    "System Notification"  // Sender
            );
        }
    }

}