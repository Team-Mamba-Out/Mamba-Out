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
        messageService.createMessage(message.getUid(), message.getTitle(), message.getText(), message.getCreateTime(), message.isRead(), message.getSender());
        return Result.success();
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

    /**
     * Retrieves a paginated list of messages for a given user ID.
     *
     * @param Uid  the user ID
     * @param size the number of messages per page (optional, default is 10)
     * @param page the current page number (optional, default is 1)
     * @return a success result containing the paginated list of messages
     */
    @GetMapping("/getMessage/{Uid}")
    public Result getMessagesByUid(@PathVariable Integer Uid,
                                   @RequestParam(required = false) Integer size,
                                   @RequestParam(required = false) Integer page) {
        // Retrieve paginated messages from the service layer
        Map<String, Object> messagesResult = messageService.getMessagesByUid(Uid, size, page);
        return Result.success(messagesResult);
    }

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
}