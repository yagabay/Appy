package com.vismus.appy.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.vismus.appy.ReminderManager;
import com.vismus.appy.R;
import com.vismus.appy.PreferenceData;

import java.util.Date;

public class WelcomePageFragment extends Fragment {

    // views
    Button _btnEnableReminders;
    Button _btnNotNow;

    WelcomePageFragmentListener _fragmentListener; // listener: MainActivity
    ReminderManager _reminderManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_welcome_page, container, false);

        // find views
        _btnEnableReminders = rootView.findViewById(R.id.btn_enable_reminders);
        _btnNotNow = rootView.findViewById(R.id.btn_not_now);

        // set listeners
        _btnEnableReminders.setOnClickListener(new OnEnableRemindersButtonClicked());
        _btnNotNow.setOnClickListener(new OnNotNowButtonClicked());

        _reminderManager = ReminderManager.getInstance(getContext());
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WelcomePageFragmentListener) {
            _fragmentListener = (WelcomePageFragmentListener) context;
        }
    }

    /* LISTENERS */

    private class OnEnableRemindersButtonClicked implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            PreferenceData prefData = PreferenceData.getInstance(getContext());
            prefData.setRemindersEnabled(true);
            prefData.setReminderSoundEnabled(true);
            Date nowDate = new Date();
            _reminderManager.setReminder(nowDate);
            closeWelcomePage();
        }
    }

    class OnNotNowButtonClicked implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            closeWelcomePage();
        }
    }

    /* HELPERS */

    void closeWelcomePage(){
        if(_fragmentListener != null) {
            _fragmentListener.onWelcomePageClosed();
        }
    }

}