package com.vismus.appy;

import java.util.Date;

public class TimeUtilsDownscaled extends TimeUtils {

    @Override
    public long getDayBeginTime(Date date) {
        return date.getTime() - (date.getTime() % MILLI_SECONDS_PER_DAY);
    }

    /* HELPERS */

    long getMinutesPerDay(){
        return 1;
    }

}
