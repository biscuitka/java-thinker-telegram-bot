package com.biscuitka.thinkerBot.config;

import com.biscuitka.thinkerBot.bot.ThinkerBot;
import com.biscuitka.thinkerBot.exception.RegistrationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@Slf4j
@AllArgsConstructor
public class BotInitializer {
    ThinkerBot bot;

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiException exp) {
            log.error("Error: {}", exp.getMessage());
            throw new RegistrationException("Ошибка регистрации бота");
        }
    }

}
