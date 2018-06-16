package com.vismus.appy;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

public class PreferenceData {

    static PreferenceData _instance = null;

    SharedPreferences _sharedPrefs;
    SharedPreferences.Editor _sharedPrefsEditor;

    public static PreferenceData getInstance(Context context){
        if(_instance == null){
            _instance = new PreferenceData(context);
        }
        return _instance;
    }

    private PreferenceData(Context context){
        _sharedPrefs = context.getSharedPreferences("AppyPreferenceData", Context.MODE_PRIVATE);
        _sharedPrefsEditor = _sharedPrefs.edit();
    }

    /* remindersEnabled */

    public void setRemindersEnabled(boolean enabled){
        _sharedPrefsEditor.putBoolean("remindersEnabled", enabled);
        _sharedPrefsEditor.commit();
    }

    public boolean getRemindersEnabled(){
        return _sharedPrefs.getBoolean("remindersEnabled", false);
    }

    /* reminderSoundEnabled */

    public void setReminderSoundEnabled(boolean enabled){
        _sharedPrefsEditor.putBoolean("reminderSoundEnabled", enabled);
        _sharedPrefsEditor.commit();
    }

    public boolean getReminderSoundEnabled(){
        return _sharedPrefs.getBoolean("reminderSoundEnabled", false);
    }

    /* prevReminderDate */

    public void setPrevReminderDate(Date date){
        _sharedPrefsEditor.putLong("prevReminderDate", date != null ? date.getTime() : -1);
        _sharedPrefsEditor.commit();
    }

    public Date getPrevReminderDate(){
        return new Date(_sharedPrefs.getLong("prevReminderDate", -1));
    }

    /* nextReminderDate */

    public void setNextReminderDate(Date date){
        _sharedPrefsEditor.putLong("nextReminderDate", date.getTime());
        _sharedPrefsEditor.commit();
    }

    public Date getNextReminderDate(){
        return new Date(_sharedPrefs.getLong("nextReminderDate", -1));
    }

    /* appEverOpened */

    public void setAppEverLaunched(boolean value){
        _sharedPrefsEditor.putBoolean("appEverLaunched", value);
        _sharedPrefsEditor.commit();
    }

    public boolean getAppEverLaunched(){
        return _sharedPrefs.getBoolean("appEverLaunched", false);
    }

    /* minReminderHour */

    public void setMinReminderHour(int hour){
        _sharedPrefsEditor.putInt("minReminderHour", hour);
        _sharedPrefsEditor.commit();
    }

    public int getMinReminderHour(){
        return _sharedPrefs.getInt("minReminderHour", ReminderManager.DEFAULT_MIN_REMINDER_HOUR);
    }

    /* maxReminderHour */

    public void setMaxReminderHour(int hour){
        _sharedPrefsEditor.putInt("maxReminderHour", hour);
        _sharedPrefsEditor.commit();
    }

    public int getMaxReminderHour(){
        return _sharedPrefs.getInt("maxReminderHour", ReminderManager.DEFAULT_MAX_REMINDER_HOUR);
    }

}
