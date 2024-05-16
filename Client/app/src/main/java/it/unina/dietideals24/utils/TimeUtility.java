package it.unina.dietideals24.utils;

import it.unina.dietideals24.exceptions.TimePickerException;

public class TimeUtility {
    private TimeUtility() {
    }

    /**
     * Formats an interval from seconds to hh:mm:ss
     *
     * @param timeInSeconds interval in seconds
     * @return the interval formatted in hh:mm:ss
     */
    public static String formatSeconds(Long timeInSeconds) {
        long days = timeInSeconds / 86400;
        long hours = (timeInSeconds / 3600) % 24;
        long secondsLeft = timeInSeconds % 60;
        long minutes = (timeInSeconds / 60) % 60;

        if (days > 0)
            return  days + "d:" + hours + "h:" + minutes + "m";
        else if (hours == 0)
            return minutes + "m:" + secondsLeft + "s";
        else
            return hours + "h:" + minutes + "m:" + secondsLeft + "s";
    }

    /**
     * Convert days, hours and minutes into milliseconds
     *
     * @param days    days to convert to milliseconds
     * @param hours   hours to convert to milliseconds
     * @param minutes minutes to convert to milliseconds
     * @return timer in milliseconds
     */
    public static long convertFieldsToMilliseconds(long days, long hours, long minutes) throws TimePickerException {
        if (days < 0 || hours > 24 || hours < 0 || minutes > 60 || minutes < 0)
            throw new TimePickerException();

        long daysInMilliseconds = days * 86400 * 1000;
        long hoursInMilliseconds = hours * 3600 * 1000;
        long minutesInMilliseconds = minutes * 60 * 1000;

        return daysInMilliseconds + hoursInMilliseconds + minutesInMilliseconds;
    }
}