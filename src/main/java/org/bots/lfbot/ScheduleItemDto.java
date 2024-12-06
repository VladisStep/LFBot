package org.bots.lfbot;

import java.util.Date;

public record ScheduleItemDto(
        Date releaseDate,
        String tvSeriesRuName,
        String episodeNumber
) {
}
