package org.bots.lfbot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bots.lfbot.config.BotConfig;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private static final Logger log = LogManager.getLogger(TelegramBot.class);

    private final SimpleDateFormat dateFormatForReleaseDate ;

    private final BotConfig botConfig;
    private final LFInfoService lfInfoService;

    public TelegramBot(BotConfig botConfig, LFInfoService lfInfoService) {
        super(botConfig.getToken());
        this.botConfig = botConfig;
        this.lfInfoService = lfInfoService;
        dateFormatForReleaseDate = new SimpleDateFormat("E, dd.M.yyyy", Locale.of("ru"));
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("Reciev message from " + update.getMessage().getChatId());
        sendMessage(update.getMessage().getChatId(), getInfo());
    }

    private void sendMessage(Long chatId, String textToSend){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend.isEmpty() ? "Нет данных" : textToSend);
        sendMessage.enableHtml(true);
        sendMessage.setReplyMarkup(createDefaultKeyboard());
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String getInfo() {
        return lfInfoService.getScheduleInfoGroupedByReleaseDate()
                .entrySet()
                .stream()
                .map(entry -> String.format(
                        "\uD83D\uDFE2 %s\n%s",
                        dateFormatForReleaseDate.format(entry.getKey()),
                        concatScheduleItems(entry.getValue())
                ))
                .collect(Collectors.joining("\n\n"));
    }

    private String concatScheduleItems(List<ScheduleItemDto> scheduleItems) {
        return scheduleItems
                .stream()
                .map(scheduleItemDto -> String.format(
                        "▪<b>%s</b> %s",
                        scheduleItemDto.tvSeriesRuName(),
                        scheduleItemDto.episodeNumber()
                ))
                .collect(Collectors.joining("\n"));
    }

    private ReplyKeyboardMarkup createDefaultKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("Test button");
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }
}
