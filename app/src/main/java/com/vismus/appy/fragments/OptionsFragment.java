package com.vismus.appy.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.vismus.appy.ReminderManager;
import com.vismus.appy.SpinnerAdapter;
import com.vismus.appy.R;
import com.vismus.appy.PreferenceData;
import com.vismus.appy.db.MoodVotesDatabase;
import com.vismus.appy.db.MoodVote;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OptionsFragment extends Fragment {

    OptionsFragmentListener _fragmentListener; // listener: MainActivity

    // views
    CheckBox _chkEnableNotifications;
    CheckBox _chkEnableNotificationsSound;
    Spinner _spnMinNotificationHour;
    Spinner _spnMaxNotificationHour;
    Button _btnExportData;
    Button _btnDeleteAppData;
    ImageButton _btnBack;

    PreferenceData _prefData;
    ReminderManager _reminderManager;
    boolean _minNotificationHourChanged;
    boolean _maxNotificationHourChanged;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_options, container, false);
        _prefData = PreferenceData.getInstance(getContext());
        _minNotificationHourChanged = false;
        _maxNotificationHourChanged = false;
        findViews(rootView);
        initViews();
        setListeners();
        _reminderManager = ReminderManager.getInstance(getContext());
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OptionsFragmentListener) {
            _fragmentListener = (OptionsFragmentListener) context;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /* LISTENERS */

    class OnEnableNotificationsCheckBoxChangedListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
            _prefData.setRemindersEnabled(isChecked);

            // update views
            _chkEnableNotificationsSound.setEnabled(isChecked);
            _spnMinNotificationHour.setEnabled(isChecked);
            _spnMaxNotificationHour.setEnabled(isChecked);

            if(isChecked){
                Date nowDate = new Date();
                _reminderManager.setReminder(nowDate);
            }
        }
    }

    class OnEnableNotificationSoundCheckBoxChangedListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
            _prefData.setReminderSoundEnabled(isChecked);
        }
    }

    class OnMinNotificationHourSpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            if(!_minNotificationHourChanged){ // item selected during view creation
                _minNotificationHourChanged = true;
                return;
            }
            _prefData.setMinReminderHour(position);
            if(position >= _spnMaxNotificationHour.getSelectedItemPosition()){
                _spnMaxNotificationHour.setSelection(position + 1);
                _prefData.setMaxReminderHour(position + 1);
            }
            Date nowDate = new Date();
            _reminderManager.setReminder(nowDate);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {}
    }

    class OnMaxNotificationHourSpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            if(!_maxNotificationHourChanged){
                _maxNotificationHourChanged = true;
                return;
            }
            _prefData.setMaxReminderHour(position);
            if(position <= _spnMinNotificationHour.getSelectedItemPosition()){
                _spnMinNotificationHour.setSelection(position - 1);
                _prefData.setMinReminderHour(position - 1);
            }
            Date nowDate = new Date();
            _reminderManager.setReminder(nowDate);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {}
    }

    class OnExportDataButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if(MoodVotesDatabase.getInstance(getContext()).moodVoteDao().getAll().size() != 0){
                exportData();
            }
            else {
                showDialogNoData();
            }
        }
    }

    class OnResetDataButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if(MoodVotesDatabase.getInstance(getContext()).moodVoteDao().getAll().size() != 0){
                showDialogConfirmDataReset();
            }
            else {
                showDialogNoData();
            }
        }
    }

    class OnBackButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    /* HELPERS */

    void findViews(ViewGroup rootView){
        _chkEnableNotifications = rootView.findViewById(R.id.chk_daily_reminder);
        _chkEnableNotificationsSound = rootView.findViewById(R.id.chk_reminder_sound);
        _spnMinNotificationHour = rootView.findViewById(R.id.spn_min_reminder_hour);
        _spnMaxNotificationHour = rootView.findViewById(R.id.spn_max_reminder_hour);
        _btnExportData = rootView.findViewById(R.id.btn_export_data);
        _btnDeleteAppData = rootView.findViewById(R.id.btn_reset_data);
        _btnBack = rootView.findViewById(R.id.btn_back);
    }

    void initViews(){
        // checkboxes
        boolean isNotificationsEnabled = _prefData.getRemindersEnabled();
        _chkEnableNotifications.setChecked(isNotificationsEnabled);
        _chkEnableNotificationsSound.setChecked(_prefData.getReminderSoundEnabled());
        _chkEnableNotificationsSound.setEnabled(isNotificationsEnabled);

        // spinners
        SpinnerAdapter adpMinNotificationsHour = new SpinnerAdapter(getContext(), R.layout.spinner_item, getResources().getStringArray(R.array.arr_day_hours));
        adpMinNotificationsHour.setItemEnabled(24, false);
        _spnMinNotificationHour.setAdapter(adpMinNotificationsHour);
        _spnMinNotificationHour.setSelection(_prefData.getMinReminderHour());
        _spnMinNotificationHour.setEnabled(isNotificationsEnabled);
        SpinnerAdapter adpMaxNotificationsHour = new SpinnerAdapter(getContext(), R.layout.spinner_item, getResources().getStringArray(R.array.arr_day_hours));
        adpMaxNotificationsHour.setItemEnabled(0, false);
        _spnMaxNotificationHour.setAdapter(adpMaxNotificationsHour);
        _spnMaxNotificationHour.setSelection(_prefData.getMaxReminderHour());
        _spnMaxNotificationHour.setEnabled(isNotificationsEnabled);
    }

    void setListeners(){
        _chkEnableNotifications.setOnCheckedChangeListener(new OnEnableNotificationsCheckBoxChangedListener());
        _chkEnableNotificationsSound.setOnCheckedChangeListener(new OnEnableNotificationSoundCheckBoxChangedListener());
        _spnMinNotificationHour.setOnItemSelectedListener(new OnMinNotificationHourSpinnerItemSelectedListener());
        _spnMaxNotificationHour.setOnItemSelectedListener(new OnMaxNotificationHourSpinnerItemSelectedListener());
        _btnExportData.setOnClickListener(new OnExportDataButtonClickListener());
        _btnDeleteAppData.setOnClickListener(new OnResetDataButtonClickListener());
        _btnBack.setOnClickListener(new OnBackButtonClickListener());
    }

    void exportData() {
        String dateStr = "";
        List<MoodVote> votes = MoodVotesDatabase.getInstance(getContext()).moodVoteDao().getAll();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
        for(MoodVote vote : votes){
            dateStr += dateFormat.format(vote.getDate()) + ", " + vote.getMood().score() + "\n";
        }
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:?subject=" + getResources().getString(R.string.export_data_email_subject) + "&body=" + dateStr));
        startActivity(intent);
    }

    void resetData(){
        MoodVotesDatabase appDatabase = MoodVotesDatabase.getInstance(getContext());
        appDatabase.moodVoteDao().deleteAll();
        _prefData.setPrevReminderDate(null);
        _fragmentListener.onAppDataReset();
    }

    void showDialogConfirmDataReset() {
        AlertDialog.Builder dlgAlertBuilder = new AlertDialog.Builder(getContext());
        dlgAlertBuilder.setMessage(getResources().getString(R.string.confirm_data_reset));
        dlgAlertBuilder.setPositiveButton("כן", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                resetData();
                if(_chkEnableNotifications.isChecked()){
                    Date nowDate = new Date();
                    _reminderManager.setReminder(nowDate);
                }
                Toast.makeText(getContext(), getResources().getString(R.string.data_was_reset), Toast.LENGTH_SHORT).show();
            }
        });
        dlgAlertBuilder.setNegativeButton("לא", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });
        AlertDialog dlgAlert = dlgAlertBuilder.create();
            dlgAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlgAlert.show();
    }

    void showDialogNoData() {
        AlertDialog.Builder dlgAlertBuilder = new AlertDialog.Builder(getContext());
        dlgAlertBuilder.setMessage(getResources().getString(R.string.no_data));
        dlgAlertBuilder.setPositiveButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });
        AlertDialog dlgAlert = dlgAlertBuilder.create();
        dlgAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlgAlert.show();
    }

}
