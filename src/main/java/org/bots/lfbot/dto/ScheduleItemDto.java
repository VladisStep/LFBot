package org.bots.lfbot.dto;

import java.util.Date;

/**
 * Schedule information item
 * <br>
 * Contains information about one title
 */
public record ScheduleItemDto(
        Date releaseDate,
        String tvSeriesRuName,
        String episodeNumber
) {
}
