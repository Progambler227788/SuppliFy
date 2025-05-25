package com.supplify.service.Implement;

import com.supplify.dto.ContactDto;
import com.supplify.dto.MessageDto;
import com.supplify.entity.Buyer;
import com.supplify.entity.Message;
import com.supplify.entity.Seller;
import com.supplify.entity.User;
import com.supplify.repository.MessageRepository;
import com.supplify.services.MessageService;
import com.supplify.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MessageServiceImpl implements MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    private final MessageRepository messageRepository;
    private final UserService userService;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository, UserService userService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
    }

    @Override
    public List<ContactDto> getAllContacts(UUID currentUserId) {
        List<Message> allMessages = messageRepository.findBySenderIdOrReceiverId(currentUserId, currentUserId);

        Set<UUID> contactIds = new HashSet<>();

        for (Message message : allMessages) {
            if (!message.getSenderId().equals(currentUserId)) {
                contactIds.add(message.getSenderId());
            }
            if (!message.getReceiverId().equals(currentUserId)) {
                contactIds.add(message.getReceiverId());
            }
        }

        List<ContactDto> contacts = new ArrayList<>();
        for (UUID contactId : contactIds) {
            Optional<?> user = userService.getUserById(contactId);
            if (user.isPresent()) {
                Object foundUser = user.get();

                if (foundUser instanceof Buyer buyer) {
                    contacts.add(new ContactDto(buyer.getId(), buyer.getFirstName().concat(" ").concat(buyer.getLastName()), buyer.getRole()));
                } else if (foundUser instanceof Seller seller) {
                    contacts.add(new ContactDto(seller.getId(), seller.getName(), seller.getRole()));
                }
            }
        }
        return contacts;
    }


    @Override
    @Transactional
    public void sendMessage(MessageDto messageDTO) {
        validateMessage(messageDTO);

        Message message = new Message();
        message.setContent(messageDTO.getContent());
        message.setSenderId(messageDTO.getSenderId());
        message.setReceiverId(messageDTO.getReceiverId());
        message.setDateTime(LocalDateTime.now());

        messageRepository.save(message);
        logger.info("Message sent from {} to {} : {}",
                messageDTO.getSenderId(),
                messageDTO.getReceiverId(), messageDTO.getContent());
    }


    @Override
    public List<MessageDto> getMessagesForUser(UUID userId, UserDetails userDetails) throws Exception {
        Optional<User> user = userService.getUserByEmail(userDetails.getUsername());

        if (user.isEmpty()) {
            throw new Exception("User not found!");
        }

        List<Message> messages = messageRepository.findBySenderIdAndReceiverId(userId, user.get().getId());
        messages.addAll(messageRepository.findBySenderIdAndReceiverId(user.get().getId(), userId));

        messages.sort(Comparator.comparing(Message::getDateTime)); // Sort messages by timestamp

        List<MessageDto> messageDtos = new ArrayList<>();
        for (Message message : messages) {
            MessageDto messageDto = new MessageDto();
            populateMessageDto(message, messageDto);
            messageDtos.add(messageDto);
        }

        return messageDtos;
    }



    @Override
    public void markMessageAsSeen(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalStateException("Message not found"));

        message.setSeen(true);
        message.setSeenTime(LocalDateTime.now());
        messageRepository.save(message);
    }

    private void populateMessageDto(Message message, MessageDto messageDto) {
        messageDto.setSenderId(message.getSenderId());
        messageDto.setReceiverId(message.getReceiverId());
        messageDto.setContent(message.getContent());
        messageDto.setTimestamp(message.getDateTime());
        messageDto.setMessageId(message.getId());
        messageDto.setSeenTime(message.getSeenTime());
        messageDto.setSeen(message.getSeen()
        );
    }

    private void validateMessage(MessageDto messageDTO) {
        if (messageDTO.getReceiverId() == null) {
            throw new IllegalArgumentException("Receiver ID is required.");
        }

        if (messageDTO.getContent() == null || messageDTO.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty.");
        }
    }

}
