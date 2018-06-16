package com.vismus.appy.db;

import android.arch.persistence.room.TypeConverter;

import com.vismus.appy.Utils;

import java.util.Date;

public class DateTypeConverter {

    @TypeConverter
    public static Date toDate(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long toLong(Date date) {
        return date == null ? null : date.getTime();
    }
}
