package org.mamba.controller;

import lombok.extern.slf4j.Slf4j;
import org.mamba.entity.Message;
import org.mamba.entity.Result;
import org.mamba.service.RecordService;
import org.mamba.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing messages.
 */
@Slf4j
@RestController
@RequestMapping("/messages")
public class MessageController {
    private RecordService recordService;

    @Autowired
    private MessageService messageService;

    /**
     * Creates a new message.
     *
     * @param message the message to create
     * @return a success result
     */
    @PostMapping("/create")
    public Result createMessage(@RequestBody Message message) {
        messageService.createMessage(message.getReceiver(), message.getTitle(), message.getText(), message.getCreateTime(), message.isRead(), message.getSender(),message.getType(),message.getRoomId());
        return Result.success();
    }

    /**
     * Counts the number of unread messages for a given user ID.
     * @param receiver the user ID
     * @return a success result containing the number of unread messages
     */
    @GetMapping("/countUnreadMessages")
    public Result countUnreadMessages(@RequestParam Integer receiver) {
        int count = messageService.countUnreadMessage(receiver);
        return Result.success(count);
    }

    /**
     * Deletes a message by its ID.
     *
     * @param id the ID of the message to delete
     * @return a success result
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteMessage(@PathVariable Integer id) {
        System.out.println(id);
        messageService.deleteMessage(id);
        return Result.success();
    }

    @GetMapping("/getSendMessage/{sender}")
    public Result getMessageBySender(@PathVariable Integer sender, @RequestParam(required = false) Integer size,
                                     @RequestParam(required = false) Integer page) {
        Map<String, Object> messagesResult = messageService.getMessagesBySender(sender, size, page);
        return Result.success(messagesResult);
    }
    /**
     * Retrieves a paginated list of messages for a given user ID.
     *
     * @param receiver  the user ID
     * @param size the number of messages per page (optional)
     * @param page the current page number (optional)
     * @return a success result containing the paginated list of messages
     */
    @GetMapping("/getMessage/{receiver}")
    public Result getMessagesByUReceiver(@PathVariable Integer receiver,
                                   @RequestParam(required = false) Integer size,
                                   @RequestParam(required = false) Integer page) {
        // Retrieve paginated messages from the service layer
        Map<String, Object> messagesResult = messageService.getMessagesByReceiver(receiver, size, page);
        return Result.success(messagesResult);
    }

    /**
     * Marks a message as read.
     * @param request the request containing the message ID
     * @return a success result
     */
    @PostMapping("/read")
    public Result readMessage(@RequestBody Map<String, Object> request) {
        Integer id = Integer.parseInt(request.get("id").toString());
        messageService.readMessage(id);

        return Result.success();
    }
    /**
     * Sends appointment reminders for records that have a start time equal to the provided time plus 10 minutes.
     *
     * @param time the provided time
     * @return a success result indicating the reminders were sent
     */
    @GetMapping("/records")
    public Result appointmentReminder(@RequestParam LocalDateTime time) {
        messageService.Reminder(time);
        return Result.success();
    }

    /**
     * Retrieves the latest messages for a user after the given timestamp.
     *
     * @param receiver the user ID
     * @param lastTimestamp the last retrieved message timestamp
     * @return a success result containing the list of new messages
     */
    @GetMapping("/latest")
    public Result getLatestMessages(@RequestParam Integer receiver, @RequestParam LocalDateTime lastTimestamp) {
        List<Message> newMessages = messageService.getMessagesAfter(receiver, lastTimestamp);
        return Result.success(newMessages);
    }
}