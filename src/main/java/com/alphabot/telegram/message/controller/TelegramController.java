package com.alphabot.telegram.message.controller;

import com.alphabot.telegram.message.controller.dto.*;
import com.alphabot.telegram.message.telegram.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
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
        telegramBot.sendTextMessage(messageDTO.getTelegramId(), messageDTO.getMessage());

        return ResponseEntity.ok().build();
    }

    @PostMapping(consumes ="application/json")
    @RequestMapping("/sendMessageWithButtons")
    public ResponseEntity<?> sendMessageWithButtons(@RequestBody MessageDTO messageDTO) {

        telegramBot.sendMessageWithButtons(messageDTO.getTelegramId(), messageDTO.getMessage());

        return ResponseEntity.ok().build();
    }

    @PostMapping(consumes ="application/json")
    @RequestMapping("/sendMessageWithInputField")
    public ResponseEntity<?> sendMessageWithInputField(@RequestBody MessageDTO messageDTO) {

        telegramBot.sendCustomInputKeyboard(messageDTO.getTelegramId());

        return ResponseEntity.ok().build();
    }
}
