package net.runelite.client.plugins.barbarianassault;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import lombok.AccessLevel;
import lombok.Getter;

class Timer
{
    @Getter(AccessLevel.PACKAGE)
    private final Instant startTime;

    Timer()
    {
        this.startTime = Instant.now();
    }

    long getElapsedTime()
    {
        return Duration.between(startTime, Instant.now()).getSeconds();
    }

    String getElapsedTimeFormatted()
    {
        return formatTime(LocalTime.ofSecondOfDay(getElapsedTime()));
    }

    private static String formatTime(LocalTime time)
    {
        if (time.getHour() > 0)
        {
            return time.format(DateTimeFormatter.ofPattern("HH:mm"));
        }
        else if (time.getMinute() > 9)
        {
            return time.format(DateTimeFormatter.ofPattern("mm:ss"));
        }
        else
        {
            return time.format(DateTimeFormatter.ofPattern("m:ss"));
        }
    }
}