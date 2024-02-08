package com.biscuitka.thinkerBot.bot;

import com.biscuitka.thinkerBot.config.BotConfig;
import com.biscuitka.thinkerBot.exception.SendMessageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ThinkerBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final String HELP_TEXT = "Привет! Я бот для сохранения цитат. Вот список доступных команд:\n\n" +
            "\uD83D\uDD35 /start - начать беседу с ботом\n" +
            "\uD83D\uDD35 /newquote - добавить новую цитату\n" +
            "\uD83D\uDD35 /myquotes - посмотреть мои цитаты\n" +
            "\uD83D\uDD35 /help - получить информацию по использованию этого бота";

    public ThinkerBot(@Value("${bot.token}") String botToken, BotConfig botConfig) {
        super(botToken);
        this.botConfig = botConfig;
        List<BotCommand> commandList = new ArrayList<>();
        commandList.add(new BotCommand("/start", "get start message"));
        commandList.add(new BotCommand("/newquote", "add new quote"));
        commandList.add(new BotCommand("/myquotes", "get my quotes"));
        commandList.add(new BotCommand("/help", "information on using this bot"));
        try {
            execute(new SetMyCommands(commandList, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException exp) {
            log.error("Command list installation error {}", exp.getMessage());
        }
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
                case "/newquote" -> sendMessage(chatId, "Здесь позднее можно добавить новую цитату");
                case "/myquotes" -> sendMessage(chatId, "Здесь позднее можно посмотреть свои цитаты");
                case "/help" -> sendMessage(chatId, HELP_TEXT);
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
