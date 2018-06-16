package com.vismus.appy.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

@Database(entities = {MoodVote.class}, version = 1)
@TypeConverters({MoodStatusTypeConverter.class, DateTypeConverter.class})
public abstract class MoodVotesDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "MoodVotes";

    private static MoodVotesDatabase INSTANCE;

    public abstract MoodVoteDao moodVoteDao();

    public static MoodVotesDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    MoodVotesDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}