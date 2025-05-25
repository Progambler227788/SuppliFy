package com.supplify.services;

import java.util.List;
import java.util.UUID;

import com.supplify.dto.ContactDto;
import com.supplify.dto.MessageDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

public interface MessageService {
    List<ContactDto> getAllContacts(UUID currentUserId);

    @Transactional
    void sendMessage(MessageDto messageDTO);

    List<MessageDto> getMessagesForUser(UUID userId, UserDetails userDetails) throws Exception;

    void markMessageAsSeen(Long messageId);
}

