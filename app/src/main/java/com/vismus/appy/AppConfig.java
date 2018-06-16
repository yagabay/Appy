package com.vismus.appy;

public class AppConfig {

    public enum RunningMode {
        REL,
        DBG
    }

    public enum TimeMode {
        NORMAL,
        DOWN_SCALED
    }

    public static final RunningMode runningMode = RunningMode.REL;

    public static final TimeMode timeMode = TimeMode.NORMAL;

}
