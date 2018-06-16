package com.vismus.appy;

import android.content.Context;

public enum AveragePeriod {

    TODAY(1, R.string.period_today),
    LAST_THREE_DAYS(3, R.string.period_last_3_days),
    LAST_WEEK(7, R.string.period_last_week),
    LAST_MONTH(30, R.string.period_last_month),
    LAST_THREE_MONTHS(90, R.string.period_last_3_months),
    LAST_SIX_MONTHS(180, R.string.period_last_6_months),
    LAST_YEAR(365, R.string.period_last_year),
    ALL(Integer.MAX_VALUE, R.string.period_all);

    static Context _context;

    int _numDays;
    int _titleResourceId;

    AveragePeriod(int numDays, int titleResourceId) {
        _numDays = numDays;
        _titleResourceId = titleResourceId;
    }

    public static void setContext(Context context){
        _context = context;
    }

    public AveragePeriod next()
    {
        return values()[(this.ordinal() + 1) % values().length];
    }

    public int numDays() {
        return _numDays;
    }

    public String title() { return _context.getResources().getString(_titleResourceId); }

}
