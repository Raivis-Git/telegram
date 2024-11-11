package com.alphabot.telegram.message.controller;

import com.alphabot.telegram.message.controller.dto.*;
import com.alphabot.telegram.message.telegram.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.util.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/telegram")
public class TelegramController {

    @Autowired
    TelegramBot telegramBot;
    Logger logger = LoggerFactory.getLogger(TelegramController.class);

    @PostMapping(consumes ="application/json")
    @RequestMapping("/sendTextMessage")
    public ResponseEntity<?> sendTextMessage(@RequestBody MessageDTO messageDTO) {

        logger.info("""
                Received sendTextMessage with:\s
                telegram Id: {}
                message: {}""", messageDTO.getTelegramId(), messageDTO.getMessage());

        if (messageDTO.getTelegramId() == null)
            return ResponseEntity.badRequest().body("telegramId is null");

        if (StringUtils.hasText(messageDTO.getMessage()))
            return ResponseEntity.badRequest().body("message is empty");

        telegramBot.sendTextMessage(messageDTO.getTelegramId(), messageDTO.getMessage());

        return ResponseEntity.ok().build();
    }

    @GetMapping
    @RequestMapping("/test")
    public ResponseEntity<?> testConnection() {
        return ResponseEntity.ok("Test successful");
    }

//    @PostMapping(consumes ="application/json")
//    @RequestMapping("/sendMessageWithButtons")
//    public ResponseEntity<?> sendMessageWithButtons(@RequestBody MessageDTO messageDTO) {
//
//        telegramBot.sendMessageWithButtons(messageDTO.getTelegramId(), messageDTO.getMessage());
//
//        return ResponseEntity.ok().build();
//    }
//
//    @PostMapping(consumes ="application/json")
//    @RequestMapping("/sendMessageWithInputField")
//    public ResponseEntity<?> sendMessageWithInputField(@RequestBody MessageDTO messageDTO) {
//
//        telegramBot.sendCustomInputKeyboard(messageDTO.getTelegramId());
//
//        return ResponseEntity.ok().build();
//    }
}
