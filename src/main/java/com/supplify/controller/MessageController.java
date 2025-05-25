package com.supplify.controller;

import com.supplify.dto.ContactDto;
import com.supplify.dto.MessageDto;
import com.supplify.entity.Product;
import com.supplify.entity.Seller;
import com.supplify.service.Implement.MessageServiceImpl;
import com.supplify.service.Implement.ProductServiceImpl;
import com.supplify.service.Implement.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/messages")
public class MessageController {

    private final MessageServiceImpl messageService;
    private final UserServiceImpl userService;
    private final ProductServiceImpl productService;

    @Autowired
    public MessageController(MessageServiceImpl messageService, UserServiceImpl userService, ProductServiceImpl productService) {
        this.messageService = messageService;
        this.userService = userService;
        this.productService = productService;
    }

    @GetMapping
    public String getMessagesPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            throw new IllegalStateException("UserDetails is null. Ensure the user is logged in.");
        }

        UUID currentUserId = getCurrentUserId(userDetails);
        String currentUserRole = getCurrentUserRole(userDetails);

        List<ContactDto> contacts = getFilteredContacts(currentUserId);

        model.addAttribute("currentUserId", currentUserId);
        model.addAttribute("currentUserRole", currentUserRole);
        model.addAttribute("currentBuyerRole", currentUserRole);
        model.addAttribute("contacts", contacts);
        model.addAttribute("SelectedSellerId", "NONE");
        model.addAttribute("SelectedSellerName", "NONE");
        model.addAttribute("SelectedSellerRole", "NONE");
        model.addAttribute("SelectedProductId", "NONE");
        return "messages";
    }

    @GetMapping("/open/{productId}")
    public String openChatWithSeller(@PathVariable Long productId, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Product product = productService.getProductById(productId);
        if (product == null) {
            throw new IllegalStateException("Product not found");
        }

        Seller foundSeller = getSellerForProduct(product);
        UUID currentUserId = getCurrentUserId(userDetails);

        List<ContactDto> contacts = getFilteredContacts(currentUserId);

        model.addAttribute("SelectedContactId", foundSeller.getId());
        model.addAttribute("SelectedContactName", foundSeller.getName());
        model.addAttribute("SelectedProductId", productId);
        model.addAttribute("currentUserId", currentUserId);
        model.addAttribute("contacts", contacts);

        return "messages";
    }

    @PostMapping("/send")
    public ResponseEntity<MessageDto> sendMessage(@RequestBody @Valid MessageDto messageDTO) {
        try {
            messageService.sendMessage(messageDTO);
        } catch (Exception e) {
            System.err.println("Error while sending message: " + e.getMessage());
            throw e;
        }
        return ResponseEntity.ok(messageDTO);
    }

    @GetMapping("/user/{userId}")
    @ResponseBody
    public ResponseEntity<List<MessageDto>> getMessagesForUser(@PathVariable UUID userId,
                                                               @AuthenticationPrincipal UserDetails userDetails) {
        try {


            List<MessageDto> messages = messageService.getMessagesForUser(userId, userDetails);

            markAsRead(userDetails, messages);

            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @PutMapping("/seen/{messageId}")
    public ResponseEntity<Void> markMessageAsSeen(@PathVariable Long messageId) {
        try {
            messageService.markMessageAsSeen(messageId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private UUID getCurrentUserId(UserDetails userDetails) {
        return userService.findIdByEmail(userDetails.getUsername());
    }

    private String getCurrentUserRole(UserDetails userDetails) {
        return userService.getRoleByEmail(userDetails.getUsername());
    }

    private List<ContactDto> getFilteredContacts(UUID currentUserId) {
        return messageService.getAllContacts(currentUserId);
    }

    private Seller getSellerForProduct(Product product) {
        return userService.getSellerById(product.getSeller().getId())
                .orElseThrow(() -> new IllegalStateException("Seller not found"));
    }

    private void markAsRead(UserDetails userDetails, List<MessageDto> messages) {
        UUID currentUserId = userService.findIdByEmail(userDetails.getUsername());

        for (MessageDto message : messages) {
            if (!message.getSeen() && !message.getSenderId().equals(currentUserId)) {
                messageService.markMessageAsSeen(message.getMessageId());
            }
        }
    }

}
