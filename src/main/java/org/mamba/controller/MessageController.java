package org.mamba.controller;

import org.mamba.entity.Message;
import org.mamba.entity.Record;
import org.mamba.entity.Result;
import org.mamba.service.RecordService;
import org.mamba.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing messages.
 */
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
        messageService.deleteMessage(id);
        return Result.success();
    }

    /**
     * Retrieves all messages for a given user ID.
     *
     * @param Uid the user ID
     *
     * @return a success result containing the list of messages
     */
    @GetMapping("/uid/{Uid}")
    public Result getMessagesByUid(@PathVariable Integer Uid) {
        List<Message> messages = messageService.getMessagesByUid(Uid);
        return Result.success(messages);
    }

    @GetMapping("/records")
    public Result getRecordsByTime(@RequestParam LocalDateTime time) {
        List<Message> messageResult = messageService.getRecordsByStartTime(time);
        return Result.success(messageResult);
    }
}