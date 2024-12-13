package org.bots.lfbot.telegram;

import lombok.extern.log4j.Log4j2;
import org.bots.lfbot.dto.ScheduleItemDto;
import org.bots.lfbot.service.LFInfoService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Log4j2
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private static final String ERROR_MESSAGE = "Ой... что-то пошло не так";
    private static final SimpleDateFormat RELEASE_DATE_FORMAT =
            new SimpleDateFormat("E, dd.M.yyyy", Locale.of("ru"));

    private final BotConfig botConfig;
    private final LFInfoService lfInfoService;

    public TelegramBot(BotConfig botConfig, LFInfoService lfInfoService) {
        super(botConfig.getToken());
        this.botConfig = botConfig;
        this.lfInfoService = lfInfoService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        log.info("Received a message \"{}\" from {}", message.getText(), chatId);
        sendMessage(chatId, getInfo());
    }

    private void sendMessage(Long chatId, String textToSend){
        try {
            execute(createMessage(chatId, textToSend));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private SendMessage createMessage(Long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend.isEmpty() ? "Нет данных" : textToSend);
        sendMessage.enableHtml(true);
        sendMessage.setReplyMarkup(createDefaultKeyboard());
        return sendMessage;
    }

    private String getInfo() {
        try {
            return lfInfoService.getScheduleInfoGroupedByReleaseDate()
                    .entrySet()
                    .stream()
                    .map(entry -> String.format(
                            "\uD83D\uDD35 %s\n%s",
                            RELEASE_DATE_FORMAT.format(entry.getKey()),
                            concatScheduleItems(entry.getValue())
                    ))
                    .collect(Collectors.joining("\n\n"));
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ERROR_MESSAGE;
        }

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
        KeyboardButton keyboardButton = new KeyboardButton();
        keyboardButton.setText("Расписание на 10 дней");
        KeyboardRow row = new KeyboardRow();
        row.add(keyboardButton);
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }
}
