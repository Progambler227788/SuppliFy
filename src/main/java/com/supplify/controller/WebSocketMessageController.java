package com.supplify.controller;

import com.supplify.dto.MessageDto;
import com.supplify.service.Implement.MessageServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketMessageController {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketMessageController.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageServiceImpl messageService;

    public WebSocketMessageController(
        SimpMessagingTemplate messagingTemplate, 
        MessageServiceImpl messageService
    ) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
    }

    @MessageMapping("/chat/sendMessage")
    public void sendMessage(@Payload MessageDto messageDto) {
        try {
            // Validate and save message
            messageService.sendMessage(messageDto);

            // Send to specific user's topic
            messagingTemplate.convertAndSend(
                "/topic/messages/" + messageDto.getReceiverId(), 
                messageDto
            );

            logger.info("Message sent successfully: {}", messageDto);
        } catch (Exception e) {
            logger.error("Error sending message", e);
        }
    }
}