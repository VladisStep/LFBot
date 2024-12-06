package org.bots.lfbot;

import org.bots.lfbot.config.BotConfig;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.stream.Collectors;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final LFInfoService lfInfoService;

    public TelegramBot(BotConfig botConfig, LFInfoService lfInfoService) {
        super(botConfig.getToken());
        this.botConfig = botConfig;
        this.lfInfoService = lfInfoService;
    }

    @Override
    public void onUpdateReceived(Update update) {

        sendMessage(
                update.getMessage().getChatId(),
                lfInfoService.getScheduleInfo()
                        .stream()
                        .map(ScheduleItemDto::tvSeriesRuName)
                        .collect(Collectors.joining("\n"))
                );
    }

    private void sendMessage(Long chatId, String textToSend){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {

        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }
}
