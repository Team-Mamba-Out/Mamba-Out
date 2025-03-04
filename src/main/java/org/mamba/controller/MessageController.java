package org.mamba.controller;

import org.mamba.entity.Message;
import org.mamba.entity.Record;
import org.mamba.entity.Result;
import org.mamba.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/create")
    public Result createMessage(@RequestBody Message message) {
        messageService.createMessage(message.getUid(), message.getTitle(), message.getText(), message.getCreateTime(), message.isRead());
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result deleteMessage(@PathVariable Integer id) {
        messageService.deleteMessage(id);
        return Result.success();
    }
}
