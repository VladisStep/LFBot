package org.bots.lfbot;

import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class LFInfoService {

    private final RestTemplate restTemplate;

    public List<ScheduleItemDto> getScheduleInfo() {

        String url = "https://www.lostfilm.download/schedule/my_0/date_ru/type_1"; // todo

        // Отправляем GET-запрос для получения HTML страницы
        String html = restTemplate.getForObject(url, String.class);

        // Парсим HTML с помощью Jsoup
        Document document = Jsoup.parse(html);

        // Пример: находим элемент по классу
        Elements elements = document.select("table.schedule-list-table > tbody > tr");

        // Пример: извлекаем текст из первого найденного элемента
        return document.select("tr")
                .stream()
                .filter(element -> !element.select("td.alpha").isEmpty())
                .map(element -> new ScheduleItemDto(
                        "",""
                ))
                .collect(Collectors.toList());
    }

}
