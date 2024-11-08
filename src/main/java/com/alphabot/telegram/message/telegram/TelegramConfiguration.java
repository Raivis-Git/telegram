package com.alphabot.telegram.message.telegram;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.telegram.telegrambots.meta.*;
import org.telegram.telegrambots.meta.exceptions.*;
import org.telegram.telegrambots.updatesreceivers.*;

@Configuration
public class TelegramConfiguration {

    @Value("${telegram.bot.token}")
    String telegramToken;

    private final Logger logger = LoggerFactory.getLogger(TelegramConfiguration.class);


    @Bean
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        return new TelegramBotsApi(DefaultBotSession.class);
    }

    @Bean
    public TelegramBot telegramBot(TelegramBotsApi telegramBotsApi) {
        try {
            TelegramBot bot = new TelegramBot(telegramToken);
            telegramBotsApi.registerBot(bot);
            logger.info("Telegram bot registered successfully");
            return bot;
        } catch (TelegramApiException e) {
            logger.error("Failed to register telegram bot", e);
            throw new BotInitializationException("Could not register telegram bot", e);
        }
    }

}

