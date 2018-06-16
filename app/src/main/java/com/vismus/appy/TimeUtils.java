package com.vismus.appy;

import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TimeUtils {

    static TimeUtils _instance = null;

    static final long OFFSET_FROM_UTC = getOffsetFromUtc();

    // mode dependant (rel/dbg) time defs
    final long MINUTES_PER_DAY = getMinutesPerDay(); // normal: 1440, down-scaled: 1
    final long SECONDS_PER_DAY = MINUTES_PER_DAY * 60;
    public final long MILLI_SECONDS_PER_DAY = SECONDS_PER_DAY * 1000;
    final long MILLI_SECONDS_PER_HOUR = MILLI_SECONDS_PER_DAY / 24;
    final long MILLI_SECONDS_PER_MINUTE = MILLI_SECONDS_PER_HOUR / 60;

    public static TimeUtils getInstance(){
        if(_instance == null){
            _instance = (AppConfig.timeMode == AppConfig.TimeMode.NORMAL) ? new TimeUtils() : new TimeUtilsDownscaled();
        }
        return _instance;
    }

    protected TimeUtils(){}

    public long getHour(Date date) {
        return (date.getTime() - getDayBeginTime(date)) / MILLI_SECONDS_PER_HOUR;
    }

    // return num days between dates (num > 0 iff date1 < date2)
    public int getDaysDiff(Date date1, Date date2) {
        return (int) ((getDayBeginTime(date2) - getDayBeginTime(date1)) / MILLI_SECONDS_PER_DAY);
    }

    // return 1 if day1 > day2, -1 if day1 < day2, 0 otherwise
    public int compareDays(Date date1, Date date2) {
        long diff = getDaysDiff(date1, date2);
        if(diff < 0){
            return 1;
        }
        else if(diff > 0){
            return -1;
        }
        return 0;
    }

    public boolean isDayEqual(Date date1, Date date2){
        return compareDays(date1, date2) == 0;
    }

    public long getDayBeginTime(Date date) {
        return date.getTime() - ((date.getTime() + OFFSET_FROM_UTC * MILLI_SECONDS_PER_HOUR) % MILLI_SECONDS_PER_DAY);
    }

    /* HELPERS */

    static long getOffsetFromUtc(){
        TimeZone timeZone = TimeZone.getDefault();
        final long baseOffset = TimeUnit.HOURS.convert(timeZone.getRawOffset(), TimeUnit.MILLISECONDS);
        return baseOffset + (timeZone.useDaylightTime() ? 1 : 0);
    }

    long getMinutesPerDay(){
        return 24 * 60;
    }

}
