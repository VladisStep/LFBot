package org.bots.lfbot;

import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class LFInfoService {

    private final static Integer DAYS_COUNT_TO_RETURN = 10;

    private final RestTemplate restTemplate;
    private final SimpleDateFormat releaseDateFormat =
            new SimpleDateFormat("E, dd.M.yyyy", Locale.of("ru"));

    public List<ScheduleItemDto> getScheduleInfo() {
        String url = "https://www.lostfilm.download/schedule/my_0/type_0"; // todo
        String html = restTemplate.getForObject(url, String.class);
        Document document = Jsoup.parse(html);
        Elements elements = document.select("table.schedule-list > tbody > tr");
        return elements.select("tr")
                .stream()
                .filter(element -> !element.select("td.placeholder").isEmpty())
                .map(this::mapElementToScheduleItemDto)
                .collect(Collectors.toList());
    }

    /**
     * Receives schedule items and groups them by release date
     *
     * @return TreeMap where keys are sorted from early date to late date
     */
    public Map<Date, List<ScheduleItemDto>> getScheduleInfoGroupedByReleaseDate() {
        return getScheduleInfo()
                .stream()
                .filter(scheduleItemDto ->
                        DateUtils.daysBetweenDates(new Date(), scheduleItemDto.releaseDate()) < DAYS_COUNT_TO_RETURN
                )
                .collect(Collectors.groupingBy(
                        ScheduleItemDto::releaseDate,
                        TreeMap::new,
                        Collectors.toList()));
    }

    private ScheduleItemDto mapElementToScheduleItemDto(Element element) {
        return new ScheduleItemDto(
                getReleaseDateFromElement(element),
                element.select("td.alpha > div > div.title-block > div.ru").text(),
                element.select("td.beta > div.serie-number-box").text()
        );
    }

    /**
     * Gets the release date from the element and parses it into the {@link Date}
     *
     * @param element html element with date
     */
    private Date getReleaseDateFromElement(Element element) {
        ;
        return element.select("td.delta")
                .textNodes()
                .stream()
                .findFirst()
                .map(textNode -> {
                    try {
                        return releaseDateFormat.parse(textNode.text().trim());
                    } catch (ParseException e) {
                        // todo
                        throw new RuntimeException(e);
                    }
                })
                // todo add custom exception
                .orElseThrow(() -> new RuntimeException("No release date"));
    }

}
