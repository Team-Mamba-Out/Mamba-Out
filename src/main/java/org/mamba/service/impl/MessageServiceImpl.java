package org.mamba.service.impl;

import org.mamba.mapper.MessageMapper;
import org.mamba.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public void createMessage(Integer Uid, String title, String text, LocalDateTime createTime, boolean isRead) {
        messageMapper.createMessage(Uid, title, text, createTime, isRead);
    }

    @Override
    public void deleteMessage(Integer id) {
        messageMapper.deleteMessage(id);
    }
}