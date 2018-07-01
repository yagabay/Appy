package com.vismus.appy;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReminderManagerDbg extends ReminderManager{

    SimpleDateFormat _dateFormat;

    static boolean _reminderPublished;

    ReminderManagerDbg(Context context){
        super(context);
        if(AppConfig.timeMode == AppConfig.TimeMode.NORMAL) {
            _dateFormat = new SimpleDateFormat("dd/MM - HH:mm:ss");
        }
        else {
            _dateFormat = new SimpleDateFormat("mm:ss");
        }
        _reminderPublished = false;
    }

    @Override
    public void publishReminder(Date nowDate, String contentTitle, String contentText){
        Toast.makeText(_context, "NOW:   " + _dateFormat.format(nowDate), Toast.LENGTH_LONG).show();
        Date reminderDate = _prefData.getNextReminderDate();
        contentText = _dateFormat.format(nowDate) + " (" + _dateFormat.format(reminderDate) + ")";
        super.publishReminder(nowDate, "Appy", contentText);
        Toast.makeText(_context, "PUBLISHED:   " + _dateFormat.format(reminderDate), Toast.LENGTH_LONG).show();
        _reminderPublished = true;
    }

    @Override
    public Date setReminder(Date nowDate) {
        if(!_reminderPublished) {
            Toast.makeText(_context, "NOW:   " + _dateFormat.format(nowDate), Toast.LENGTH_LONG).show();
        }
        Date reminderDate = super.setReminder(nowDate);
        Toast.makeText(_context, "SET:   " + _dateFormat.format(reminderDate), Toast.LENGTH_LONG).show();
        _reminderPublished = false;
        return reminderDate;
    }

    /* HELPERS */

    void printTimes(long todayStartTime, long nowTime, long minHour, long maxHour, long notifyTime){
        long time = todayStartTime;
        String todayStr = "TODAY:\n\n";
        for(int i = 0; i < 24; ++i){
            if(nowTime >= time && nowTime < time + _timeUtils.MILLI_SECONDS_PER_HOUR){
                todayStr += "X";
            }
            if(notifyTime >= time && notifyTime < time + _timeUtils.MILLI_SECONDS_PER_HOUR){
                todayStr += "@";
            }
            if(i >= minHour && i < maxHour){
                todayStr += "*";
            }
            todayStr += "" + i + ": " + _dateFormat.format(new Date(time));
            if(i != 23){
                todayStr += "\n";
            }
            time += _timeUtils.MILLI_SECONDS_PER_HOUR;
        }
        Toast toast1 = Toast.makeText(_context, todayStr, Toast.LENGTH_LONG);
        ViewGroup viewGroup1 = (ViewGroup) toast1.getView();
        TextView textView1 = (TextView) viewGroup1.getChildAt(0);
        textView1.setTextSize(13);
        toast1.show();
        time = todayStartTime + _timeUtils.MILLI_SECONDS_PER_DAY;
        String tomorrowStr = "TOMORROW:\n\n";
        for(int i = 0; i < 24; ++i){
            if(notifyTime >= time && notifyTime < time + _timeUtils.MILLI_SECONDS_PER_HOUR){
                tomorrowStr += "@";
            }
            if(i >= minHour && i < maxHour){
                tomorrowStr += "*";
            }
            tomorrowStr += "" + i + ": " + _dateFormat.format(new Date(time));
            if(i != 23){
                tomorrowStr += "\n";
            }
            time += _timeUtils.MILLI_SECONDS_PER_HOUR;
        }
        Toast toast2 = Toast.makeText(_context, tomorrowStr, Toast.LENGTH_LONG);
        ViewGroup viewGroup2 = (ViewGroup) toast2.getView();
        TextView textView2 = (TextView) viewGroup2.getChildAt(0);
        textView2.setTextSize(13);
        toast2.show();
    }

}