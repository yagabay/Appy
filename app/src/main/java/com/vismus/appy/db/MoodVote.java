package com.vismus.appy.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.vismus.appy.Mood;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity
public class MoodVote {

    // members

    @PrimaryKey(autoGenerate = true)
    int _id;

    Mood _mood;

    Date _date;

    // methods

    public MoodVote(Mood mood, Date date){
        _mood = mood;
        _date = date;
    }

    public int getId() { return _id; }

    public void setId(int id) {
        _id = id;
    }

    public Mood getMood() { return _mood; }

    public Date getDate() { return _date; }

}