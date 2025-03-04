package org.mamba.service;

import java.time.LocalDateTime;

public interface MessageService {
    void createMessage(Integer Uid, String title, String text, LocalDateTime createTime, boolean isRead);
    void deleteMessage(Integer id);
}