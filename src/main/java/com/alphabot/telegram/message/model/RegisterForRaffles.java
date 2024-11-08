package com.alphabot.telegram.message.model;

public class RegisterForRaffles {

    private Long telegramId;
    private String telegramUserName;
    private String raffleKey;

    public RegisterForRaffles(Long telegramId, String telegramUserName, String raffleKey) {
        this.telegramId = telegramId;
        this.telegramUserName = telegramUserName;
        this.raffleKey = raffleKey;
    }

    public RegisterForRaffles() {
    }

    public Long getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(Long telegramId) {
        this.telegramId = telegramId;
    }

    public String getTelegramUserName() {
        return telegramUserName;
    }

    public void setTelegramUserName(String telegramUserName) {
        this.telegramUserName = telegramUserName;
    }

    public String getRaffleKey() {
        return raffleKey;
    }

    public void setRaffleKey(String raffleKey) {
        this.raffleKey = raffleKey;
    }
}
