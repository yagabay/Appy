package com.vismus.appy;

import android.content.Context;
import android.view.View;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.vismus.appy.db.MoodVotesDatabase;
import com.vismus.appy.db.MoodVote;

import java.util.Date;
import java.util.List;
import java.util.Random;

public class Utils {

    static final TimeUtils _timeUtils = TimeUtils.getInstance();

    public static LinearLayout getMoodIcon(Context context, Mood mood) {
        return getMixedMoodIcon(context, mood, mood);
    }

    public static LinearLayout getMixedMoodIcon(Context context, double value) {
        double valueRoundedToHalf = (double) Math.round(value * 2) / 2;
        int intValueBelow = (int) valueRoundedToHalf;
        int intValueAbove = (intValueBelow == valueRoundedToHalf) ? intValueBelow : intValueBelow + 1;
        return Utils.getMixedMoodIcon(context, Mood.valueOf(intValueBelow), Mood.valueOf(intValueAbove));
    }

    public static LinearLayout getMixedMoodIcon(Context context, Mood leftHalfMood, Mood rightHalfMood) {

        // resources ids
        int leftHalfMoodResId = leftHalfMood.resourceId();
        int rightHalfMoodResId = rightHalfMood.resourceId();
        if (leftHalfMood == Mood.EXTREMELY_SAD && rightHalfMood == Mood.EXTREMELY_SAD) {
            leftHalfMoodResId = R.drawable.ic_mood_extremely_sad_with_tear;
        }

        // left half image view
        ImageView imvLeftHalfMoodIcon = new ImageView(context);
        imvLeftHalfMoodIcon.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        imvLeftHalfMoodIcon.setImageResource(leftHalfMoodResId);

        // right half image view
        ImageView imvRightHalfMoodIcon = new ImageView(context);
        imvRightHalfMoodIcon.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        imvRightHalfMoodIcon.setImageResource(rightHalfMoodResId);
        imvRightHalfMoodIcon.setScaleX(-1);
        imvRightHalfMoodIcon.setBackgroundColor(312323);

        // hosting layout
        LinearLayout layMoodIcon = new LinearLayout(context);
        layMoodIcon.setOrientation(LinearLayout.HORIZONTAL);
        layMoodIcon.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        layMoodIcon.addView(imvLeftHalfMoodIcon);
        if (leftHalfMood != rightHalfMood) {
            LinearLayout laySeparator = new LinearLayout(context);
            laySeparator.setLayoutParams(new FrameLayout.LayoutParams(1, FrameLayout.LayoutParams.MATCH_PARENT));
            layMoodIcon.addView(laySeparator);
        }
        layMoodIcon.addView(imvRightHalfMoodIcon);
        layMoodIcon.setTag(AverageListAdapter.AverageInfoDisplay.AVERAGE_DISPLAY_ICON);
        return layMoodIcon;
    }

    public static int getWidestView(Context context, Adapter adapter) {
        int maxWidth = 0;
        View view = null;
        FrameLayout fakeParent = new FrameLayout(context);
        for (int i = 0, count = adapter.getCount(); i < count; i++) {
            view = adapter.getView(i, view, fakeParent);
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int width = view.getMeasuredWidth();
            if (width > maxWidth) {
                maxWidth = width;
            }
        }
        return maxWidth;
    }

    public static void addMoodVote(Context context, Mood mood, Date date, boolean overwriteLastVote) {
        MoodVotesDatabase appDatabase = MoodVotesDatabase.getInstance(context);
        List<MoodVote> moodVotes = appDatabase.moodVoteDao().getAll();
        if (overwriteLastVote) {
            appDatabase.moodVoteDao().delete(moodVotes.get(moodVotes.size() - 1));
        }
        appDatabase.moodVoteDao().insert(new MoodVote(mood, date));
    }

    public static Date getLastVoteDate(Context context) {
        MoodVotesDatabase appDatabase = MoodVotesDatabase.getInstance(context);
        List<MoodVote> moodVotes = appDatabase.moodVoteDao().getAll();
        if (moodVotes.size() == 0) {
            return null;
        }
        return moodVotes.get(moodVotes.size() - 1).getDate();
    }

    public static boolean votedToday(Context context, Date nowDate) {
        Date lastVoteDate = getLastVoteDate(context);
        return lastVoteDate != null ? _timeUtils.isDayEqual(lastVoteDate, nowDate) : false;
    }

    public static boolean remindedToday(Context context, Date nowDate) {
        PreferenceData prefData = PreferenceData.getInstance(context);
        return _timeUtils.isDayEqual(prefData.getPrevReminderDate(), nowDate);
    }

    public static boolean isDeviceTimeValid(Context context){
        Date nowDate = new Date();
        List<MoodVote> moodVotes = MoodVotesDatabase.getInstance(context).moodVoteDao().getAll();
        Date lastVoteDate = (moodVotes.size() != 0) ?  moodVotes.get(moodVotes.size() - 1).getDate() : null;
        if(lastVoteDate == null){
            return true;
        }
        return (_timeUtils.compareDays(lastVoteDate, nowDate) != 1); // day of last vote > today
    }

    public static long randomize(long min, long max) {
        return new Random().nextInt((int) (max - min)) + min;
    }

}
