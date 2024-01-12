package com.biscuitka.thinkerBot.bot;

import com.biscuitka.thinkerBot.config.BotConfig;
import com.biscuitka.thinkerBot.exception.SendMessageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class ThinkerBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;

    public ThinkerBot(@Value("${bot.token}") String botToken, BotConfig botConfig) {
        super(botToken);
        this.botConfig = botConfig;
    }


    @Override
    public String getBotUsername() {
        return botConfig.getBotUserName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getChat().getFirstName();

            switch (messageText) {
                case "/start" -> replyStartCommand(chatId, userName);
                case "/1" -> sendMessage(chatId, " " );
                default -> sendMessage(chatId, "Такого я еще не умею \uD83E\uDDD9\u200D♂️");
            }
        }
    }

    private void replyStartCommand(long chatId, String userName) {
        String answer = "Приветствую тебя, " + userName + ",\uD83E\uDD17 Приятно познакомиться! " +
                "\n Вот что я умею: " +
                "\n \uD83D\uDC49 сохранять цитаты, которые тебе понравились " +
                "\n \uD83D\uDC49 выдавать сохраненные цитаты" +
                "\n \uD83D\uDC49 и что-то еще";
        log.info("Answer to user: {}", userName);
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException exp) {
            log.error("Error: {}", exp.getMessage());
            throw new SendMessageException("Ошибка отправки сообщения");
        }
    }

}
