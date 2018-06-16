package com.vismus.appy;

public enum AveragePeriod {

    TODAY(1, "היום"),
    LAST_THREE_DAYS(3, "3 הימים האחרונים"),
    LAST_WEEK(7, "השבוע האחרון"),
    LAST_MONTH(30, "החודש האחרון"),
    LAST_THREE_MONTHS(90, "3 החודשים האחרונים"),
    LAST_SIX_MONTHS(180, "חצי השנה האחרונה"),
    LAST_YEAR(365, "השנה האחרונה"),
    ALL(Integer.MAX_VALUE, "הכל");

    int _numDays;
    String _title;

    AveragePeriod(int numDays, String title) {
        _numDays = numDays;
        _title = title;
    }

    public AveragePeriod next()
    {
        return values()[(this.ordinal() + 1) % values().length];
    }

    public int numDays() {
        return _numDays;
    }

    public String title() { return _title; }
}
