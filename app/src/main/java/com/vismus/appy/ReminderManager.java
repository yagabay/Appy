package com.vismus.appy;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;

import com.vismus.appy.activities.MainActivity;
import java.util.Date;

public class ReminderManager {

    static ReminderManager _instance = null;

    enum ReminderDay {
        TODAY,
        TOMORROW
    }

    static final int NOTIFICATION_ID = 1;
    static final String NOISY_NOTIFICATION_CHANNEL_ID = "CHANNEL_NOISY";
    static final String SILENT_NOTIFICATION_CHANNEL_ID = "CHANNEL_SILENT";

    static final int DEFAULT_MIN_REMINDER_HOUR = 10;
    static final int DEFAULT_MAX_REMINDER_HOUR = 20;

    Context _context;
    PreferenceData _prefData;
    TimeUtils _timeUtils = TimeUtils.getInstance();

    /* safety margins prevent wrong behavior caused by built-in notifications inaccuracy:
       minReminderHour + margin: prevents randomizing time which older than the actual time reminder is set to (?)
       maxReminderHour - margin: prevents reminders from publish beyond the maximal hour */
    final long SAFETY_MARGIN_MILLI_SECS = 5 * _timeUtils.MILLI_SECONDS_PER_MINUTE;

    public static ReminderManager getInstance(Context context){
        if(_instance == null){
            _instance = (AppConfig.runningMode == AppConfig.RunningMode.REL) ? new ReminderManager(context) : new ReminderManagerDbg(context);
        }
        return _instance;
    }

    protected ReminderManager(Context context){
        _context = context;
        _prefData = PreferenceData.getInstance(context);
        createNotificationChannels();
    }

    public boolean isRemindersEnabled(){
        return _prefData.getRemindersEnabled();
    }

    public void publishReminder(Date nowDate) {
        String appName = _context.getResources().getString(R.string.app_name);
        String voteQuestion = _context.getResources().getString(R.string.vote_question);
        publishReminder(nowDate, appName, voteQuestion);
    }

    public Date setReminder(Date nowDate) {
        Date reminderDate;
        if (!Utils.votedToday(_context, nowDate) && !Utils.remindedToday(_context, nowDate)) { // next reminder should be set for today
            long todayReminderMinTime = getNextReminderMinTime(ReminderDay.TODAY, nowDate);
            long todayReminderMaxTime = getNextReminderMaxTime(ReminderDay.TODAY, nowDate);
            if(todayReminderMinTime <= todayReminderMaxTime) { // next reminder can be set for today
                reminderDate = setReminderForToday(nowDate);
            }
            else{
                reminderDate = setReminderForTomorrow(nowDate);
            }
        }
        else{
            reminderDate = setReminderForTomorrow(nowDate);
        }
        _prefData.setNextReminderDate(reminderDate);
        return reminderDate;
    }

    public boolean isHourInRange(Date date){
        long hour = _timeUtils.getHour(date);
        return (hour >= _prefData.getMinReminderHour() && hour < _prefData.getMaxReminderHour());
    }

    /* HELPERS */

    protected void publishReminder(Date nowDate, String contentTitle, String contentText){
        Notification notification = createNotification(contentTitle, contentText);
        NotificationManager notificationManager = (NotificationManager) _context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
        _prefData.setPrevReminderDate(nowDate);
    }

    long getNextReminderMinTime(ReminderDay reminderDay, Date nowDate){
        int minReminderHour = _prefData.getMinReminderHour();
        if(reminderDay == ReminderDay.TODAY) {
            long todayBeginTime = _timeUtils.getDayBeginTime(nowDate);
            return Math.max(nowDate.getTime(), todayBeginTime + minReminderHour * _timeUtils.MILLI_SECONDS_PER_HOUR) + SAFETY_MARGIN_MILLI_SECS;
        }
        else{
            long tomorrowBeginTime = _timeUtils.getDayBeginTime(nowDate) + _timeUtils.MILLI_SECONDS_PER_DAY;
            return tomorrowBeginTime + minReminderHour * _timeUtils.MILLI_SECONDS_PER_HOUR + SAFETY_MARGIN_MILLI_SECS;
        }
    }

    long getNextReminderMaxTime(ReminderDay nextReminderDay, Date nowDate){
        int maxReminderHour = _prefData.getMaxReminderHour();
        if(nextReminderDay == ReminderDay.TODAY) {
            long todayBeginTime = _timeUtils.getDayBeginTime(nowDate);
            return todayBeginTime + maxReminderHour * _timeUtils.MILLI_SECONDS_PER_HOUR - SAFETY_MARGIN_MILLI_SECS;
        }
        else{
            long tomorrowBeginTime = _timeUtils.getDayBeginTime(nowDate) + _timeUtils.MILLI_SECONDS_PER_DAY;
            return tomorrowBeginTime + maxReminderHour * _timeUtils.MILLI_SECONDS_PER_HOUR - SAFETY_MARGIN_MILLI_SECS;
        }
    }

    Date setReminderForToday(Date nowDate){
        long minReminderTime = getNextReminderMinTime(ReminderDay.TODAY, nowDate);
        long maxReminderTime = getNextReminderMaxTime(ReminderDay.TODAY, nowDate);
        return setReminderInTimeRange(minReminderTime, maxReminderTime);
    }

    Date setReminderForTomorrow(Date nowDate){
        long minReminderTime = getNextReminderMinTime(ReminderDay.TOMORROW, nowDate);
        long maxReminderTime = getNextReminderMaxTime(ReminderDay.TOMORROW, nowDate);
        return setReminderInTimeRange(minReminderTime, maxReminderTime);
    }

    Date setReminderInTimeRange(long minReminderTime, long maxReminderTime){
        long reminderTime = Utils.randomize(minReminderTime, maxReminderTime);
        Intent intent = new Intent(_context, ReminderPublisher.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(_context, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
        return new Date(reminderTime);
    }

    Notification createNotification(String contentTitle, String contentText){
        boolean isReminderSoundEnabled = _prefData.getReminderSoundEnabled();
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channel = isReminderSoundEnabled ? NOISY_NOTIFICATION_CHANNEL_ID : SILENT_NOTIFICATION_CHANNEL_ID;
            builder = new Notification.Builder(_context, channel)
                    .setChannelId(channel)
                    .setSmallIcon(R.drawable.logo_appy)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText);
        }
        else{
            builder = new Notification.Builder(_context)
                    .setSmallIcon(R.drawable.logo_appy)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText);
            if(isReminderSoundEnabled){
                builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            }
        }
        Intent intent = new Intent(_context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(_context, NOTIFICATION_ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Notification notification = builder.setContentIntent(pendingIntent).build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        return notification;
    }

    void createNotificationChannels(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) _context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel noisyChannel = new NotificationChannel(NOISY_NOTIFICATION_CHANNEL_ID, NOISY_NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(noisyChannel);
            NotificationChannel silentChannel = new NotificationChannel(SILENT_NOTIFICATION_CHANNEL_ID, SILENT_NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH);
            silentChannel.setSound(null, null);
            manager.createNotificationChannel(silentChannel);
        }
    }

}
