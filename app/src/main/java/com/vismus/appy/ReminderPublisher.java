package com.vismus.appy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Date;

public class ReminderPublisher extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        Date nowDate = new Date();
        ReminderManager reminderManager = ReminderManager.getInstance(context);
        if (reminderManager.isRemindersEnabled()) {
            if (!Utils.votedToday(context, nowDate) && !Utils.remindedToday(context, nowDate) &&
                    reminderManager.isHourInRange(nowDate)){
                reminderManager.publishReminder(nowDate);
            }
            reminderManager.setReminder(nowDate);
        }
    }
}