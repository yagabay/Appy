package com.vismus.appy.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MoodVoteDao {

    @Query("SELECT * FROM MoodVote")
    List<MoodVote> getAll();

    @Query("SELECT * FROM MoodVote WHERE _id = :id")
    MoodVote getMoodEntryById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(MoodVote... moodEntries);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MoodVote moodEntry);

    @Delete
    void delete(MoodVote moodEntry);

    @Query("DELETE FROM MoodVote")
    void deleteAll();

}
