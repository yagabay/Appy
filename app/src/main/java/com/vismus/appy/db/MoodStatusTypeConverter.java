package com.vismus.appy.db;

import android.arch.persistence.room.TypeConverter;

import com.vismus.appy.Mood;

public class MoodStatusTypeConverter {

    @TypeConverter
    public static Mood toMoodStatus(Integer value) {
        return value == null ? null : Mood.valueOf(value);
    }

    @TypeConverter
    public static Integer toInteger(Mood moodStatus) {
        return moodStatus == null ? null : moodStatus.score();
    }

}
