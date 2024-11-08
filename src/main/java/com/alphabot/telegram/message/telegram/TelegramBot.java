package com.alphabot.telegram.message.telegram;

import ch.qos.logback.core.util.*;
import com.alphabot.telegram.message.model.*;
import com.alphabot.telegram.message.service.*;
import org.slf4j.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.telegram.telegrambots.bots.*;
import org.telegram.telegrambots.meta.api.methods.*;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.*;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.*;
import org.telegram.telegrambots.meta.exceptions.*;

import java.util.*;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final static String apiKeyReply = "Enter your raffle api key";
    private final Logger logger = LoggerFactory.getLogger(TelegramBot.class);

    @Autowired
    ApiService apiService;

    public TelegramBot(@Value("${telegram.bot.token}")String telegramToken) {
        super(telegramToken);
    }

    // Basic text message
    public void sendTextMessage(Long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .parseMode(ParseMode.HTML)  // Supports HTML formatting
                .build();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Error sending message: ", e);
        }
    }

    // Message with buttons
    public void sendMessageWithButtons(Long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .replyMarkup(createKeyboardMarkup())
                .build();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Error sending message with buttons: ", e);
        }
    }

    // Create inline keyboard
    private InlineKeyboardMarkup createKeyboardMarkup() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        // First row
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Button 1");
        button1.setCallbackData("button1_pressed");
        rowInline1.add(button1);

        // Add rows to keyboard
        rowsInline.add(rowInline1);
        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }
    // Send input field
    public void sendMessageToReplyTo(Long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .replyMarkup(ForceReplyKeyboard.builder()
                        .forceReply(true)
                        .inputFieldPlaceholder("Type here...")
                        .selective(true)
                        .build())
                .build();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendCustomInputKeyboard(Long chatId) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        KeyboardButton button = KeyboardButton.builder()
                .text("Share Input")
                .requestContact(false) // Set to true if you want to request phone number
                .requestLocation(false) // Set to true if you want to request location
                .build();

        row.add(button);
        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text("Click the button below to input:")
                .replyMarkup(keyboardMarkup)
                .build();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // Send photo
    public void sendPhoto(Long chatId, String caption, String photoPath) {
        SendPhoto photo = SendPhoto.builder()
                .chatId(chatId.toString())
                .photo(new InputFile(new java.io.File(photoPath)))
                .caption(caption)
                .build();

        try {
            execute(photo);
        } catch (TelegramApiException e) {
            logger.error("Error sending photo: ", e);
        }
    }

    // Send document
    public void sendDocument(Long chatId, String caption, String documentPath) {
        SendDocument document = SendDocument.builder()
                .chatId(chatId.toString())
                .document(new InputFile(new java.io.File(documentPath)))
                .caption(caption)
                .build();

        try {
            execute(document);
        } catch (TelegramApiException e) {
            logger.error("Error sending document: ", e);
        }
    }

    // Send location
    public void sendLocation(Long chatId, Double latitude, Double longitude) {
        SendLocation location = SendLocation.builder()
                .chatId(chatId.toString())
                .latitude(latitude)
                .longitude(longitude)
                .build();

        try {
            execute(location);
        } catch (TelegramApiException e) {
            logger.error("Error sending location: ", e);
        }
    }

    // Message with reply keyboard
    public void sendMessageWithReplyKeyboard(Long chatId, String text) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        // First row
        KeyboardRow row1 = new KeyboardRow();
        row1.add("Button 1");
        row1.add("Button 2");

        // Second row
        KeyboardRow row2 = new KeyboardRow();
        row2.add("Button 3");
        row2.add("Button 4");

        keyboard.add(row1);
        keyboard.add(row2);
        keyboardMarkup.setKeyboard(keyboard);

        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .replyMarkup(keyboardMarkup)
                .build();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Error sending message with reply keyboard: ", e);
        }
    }

    // Edit message
    public void editMessage(Long chatId, Integer messageId, String newText) {
        EditMessageText editMessage = EditMessageText.builder()
                .chatId(chatId.toString())
                .messageId(messageId)
                .text(newText)
                .build();

        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            logger.error("Error editing message: ", e);
        }
    }

    // Delete message
    public void deleteMessage(Long chatId, Integer messageId) {
        DeleteMessage deleteMessage = DeleteMessage.builder()
                .chatId(chatId.toString())
                .messageId(messageId)
                .build();

        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            logger.error("Error deleting message: ", e);
        }
    }

    // Send message with HTML formatting
    public void sendFormattedMessage(Long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .parseMode(ParseMode.HTML)
                .build();

        try {
            execute(message);
        } catch (TelegramApiException ignore) {}
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.getMessage().isReply())
            responseOnReply(update);
        else if (update.hasMessage() && update.getMessage().hasText())
            responseOnReceivedText(update);
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }

    @Override
    public String getBotUsername() {
        return "RaffleTheBot";
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }

    private void handleCommand(String command, long chatId) {
        // Handle specific commands
        switch (command) {
            case "/start" ->
                // Handle start command
                System.out.println("Start command received");
            case "/help" ->
                // Handle help command
                System.out.println("Help command received");
            case "/raffleConnect" ->
                sendMessageToReplyTo(chatId, apiKeyReply);
            case "/ableToReceiveMessages" -> {

            }
            default -> {}
                // Handle unknown command
        }
        System.out.println("Received command");
    }

    private void handleMessage(String message, long chatId) {
        // Handle regular messages
        System.out.println("Regular message received: " + message);
    }

    private void responseOnReceivedText(Update update) {
        String messageText = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        String userName = update.getMessage().getFrom().getFirstName();

        System.out.println("Received message: " + messageText);
        System.out.println("From user: " + userName);
        System.out.println("Chat ID: " + chatId);

        // Handle different types of messages
        if (messageText.startsWith("/")) {
            // Handle commands
            handleCommand(messageText, chatId);
        } else {
            // Handle regular messages
            handleMessage(messageText, chatId);
        }
    }

    private void responseOnReply(Update update) {
        Message message = update.getMessage();
        String messageText = message.getText();
        Long chatId = message.getChatId();

        String username = message.getFrom().getUserName();
        if (StringUtil.isNullOrEmpty(username))
            username = message.getFrom().getFirstName();

        System.out.println("Received message: " + messageText);
        System.out.println("From user with id: " + chatId);
        System.out.println("From user with name: " + username);
        System.out.println("chat id" + chatId);
        System.out.println("reply to chat id" + message.getReplyToMessage().getFrom().getId());

        if (!chatId.equals(message.getReplyToMessage().getFrom().getId())
                && apiKeyReply.equals(message.getReplyToMessage().getText())) {

            String responseText;
            RegisterForRafflesResponse registerResponse = apiService.registerForRaffles(new RegisterForRaffles(chatId, username, messageText));
            if (registerResponse.getSuccess())
                responseText = "You are now able to receive messages on telegram about raffles";
            else
                responseText = "Couldn't register: " + registerResponse.getMessage();

            SendMessage response = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(responseText)
                    .build();

            try {
                execute(response);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
