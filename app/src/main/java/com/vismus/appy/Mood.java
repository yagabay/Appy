package com.vismus.appy;

import java.util.HashMap;
import java.util.Map;

public enum Mood {

    EXTREMELY_SAD(1, R.drawable.ic_mood_extremely_sad),
    FAIRLY_SAD(2, R.drawable.ic_mood_fairly_sad),
    SLIGHTLY_SAD(3, R.drawable.ic_mood_slightly_sad),
    NEUTRAL(4, R.drawable.ic_mood_neutral),
    SLIGHTLY_HAPPY(5, R.drawable.ic_mood_slightly_happy),
    FAIRLY_HAPPY(6, R.drawable.ic_mood_fairly_happy),
    EXTREMELY_HAPPY(7, R.drawable.ic_mood_extremely_happy);

    int _index;
    int _iconResourceId;

    static Map moods = new HashMap<>();

    Mood(int index, int iconResourceId) {
        _index = index;
        _iconResourceId = iconResourceId;
    }

    static {
        for (Mood mood : Mood.values()) {
            moods.put(mood._index, mood);
        }
    }

    public int score() {
        return _index;
    }

    public int resourceId() { return _iconResourceId; }

    public static Mood valueOf(int index) {
        return (Mood) moods.get(index);
    }
}
