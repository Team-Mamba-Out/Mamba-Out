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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * @param receiver        the user ID
     * @param title      the title of the message
     * @param text       the text of the message
     * @param createTime the creation time of the message
     * @param isRead     the read status of the message
     */
    @Override
    public void createMessage(Integer receiver, String title, String text, LocalDateTime createTime, Boolean isRead, String sender, Integer type, Integer roomId) {
        messageMapper.createMessage(receiver, title, text, createTime, isRead, sender, type, roomId);
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

    @Override
    public Map<String, Object> getMessagesBySender(Integer sender, Integer size, Integer page) {
        // Calculate offset
        Integer offset = null;
        if (size != null && page != null) {
            offset = (page - 1) * size;
        }
        // Retrieve the paginated list of messages
        List<Message> messageList = messageMapper.getMessagesBySender(sender,null, size, offset);

        // Retrieve the total number of messages for the given user ID
        int total = messageMapper.getMessagesCountBySender(sender);

        // Calculate the total number of pages
        Integer totalPage = null;
        if (size != null) {
            totalPage = total % size == 0 ? total / size : total / size + 1;
        }
        // Prepare the response map
        Map<String, Object> result = new HashMap<>();
        result.put("messages", messageList);
        result.put("totalPage", totalPage);
        result.put("total", total);
        result.put("pageNumber", page);
        return result;
    }


    /**
     * Retrieves a paginated list of messages for a given user ID.
     *
     * @param receiver  the user ID
     * @param size the number of messages per page
     * @param page the current page number
     * @return a map containing the list of messages, total pages, total messages, and current page number
     */
    @Override
    public Map<String, Object> getMessagesByReceiver(Integer receiver, Integer size, Integer page) {
        // Calculate offset
        Integer offset = null;
        if (size != null && page != null) {
            offset = (page - 1) * size;
        }
        // Retrieve the paginated list of messages
        List<Message> messageList = messageMapper.getMessagesByReceiver(receiver,null, size, offset);

        // Retrieve the total number of messages for the given user ID
        int total = messageMapper.getMessagesCountByUid(receiver);

        // Calculate the total number of pages
        Integer totalPage = null;
        if (size != null) {
            totalPage = total % size == 0 ? total / size : total / size + 1;
        }
        // Prepare the response map
        Map<String, Object> result = new HashMap<>();
        result.put("messages", messageList);
        result.put("totalPage", totalPage);
        result.put("total", total);
        result.put("pageNumber", page);
        return result;
    }

    /**
     * * Obtains the list of records that have a start time equal to the provided start time.
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
                    "1;Jinhao Zhang"  // Sender
                    , 0, record.getRoomId()
            );
        }
    }

    @Override
    public void readMessage(Integer id) {
        messageMapper.updateIsRead(id);
    }

    @Override
    public List<Message> getMessagesAfter(Integer receiver, LocalDateTime lastTimestamp) {
        return messageMapper.getMessagesAfter(receiver, lastTimestamp);
    }

}