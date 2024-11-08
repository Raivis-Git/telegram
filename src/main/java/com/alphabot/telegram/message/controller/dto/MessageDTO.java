package com.alphabot.telegram.message.controller.dto;

public class MessageDTO {
    Long telegramId;
    String message;

    public MessageDTO(Long telegramId, String message) {
        this.telegramId = telegramId;
        this.message = message;
    }

    public MessageDTO() {
    }

    public Long getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(Long telegramId) {
        this.telegramId = telegramId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
