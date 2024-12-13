package org.bots.lfbot.utils;

import lombok.experimental.UtilityClass;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@UtilityClass
public final class DateUtils {

    /**
     * @return the number of days between two {@link java.util.Date}
     */
    public static long daysBetweenDates(Date firstDate, Date secondDate) {
        long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
}
