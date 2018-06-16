package com.vismus.appy;

import java.util.HashMap;
import java.util.Map;

public enum Mood {

    EXTREMELY_SAD(1, "Extremely Sad", R.drawable.ic_mood_extremely_sad),
    FAIRLY_SAD(2, "Fairly Sad", R.drawable.ic_mood_fairly_sad),
    SLIGHTLY_SAD(3, "Slightly Sad", R.drawable.ic_mood_slightly_sad),
    NEUTRAL(4, "Neutral", R.drawable.ic_mood_neutral),
    SLIGHTLY_HAPPY(5, "Slightly Happy", R.drawable.ic_mood_slightly_happy),
    FAIRLY_HAPPY(6, "Fairly Happy", R.drawable.ic_mood_fairly_happy),
    EXTREMELY_HAPPY(7, "Extremely Happy", R.drawable.ic_mood_extremely_happy);

    int _index;
    String _title;
    int _resourceId;

    static Map moods = new HashMap<>();

    Mood(int index, String title, int resourceId) {
        _index = index;
        _title = title;
        _resourceId = resourceId;
    }

    static {
        for (Mood mood : Mood.values()) {
            moods.put(mood._index, mood);
        }
    }

    public int score() {
        return _index;
    }

    public String title() { return _title; }

    public int resourceId() { return _resourceId; }

    public static Mood valueOf(int index) {
        return (Mood) moods.get(index);
    }
}
