package com.vismus.appy.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.vismus.appy.Mood;
import com.vismus.appy.VoteListAdapter;
import com.vismus.appy.R;
import com.vismus.appy.Utils;

import java.util.Date;

public class VoteFragment extends Fragment {

    Context _context;

    VoteListAdapter _voteListAdapter;
    VoteFragmentListener _fragmentListener; // listener: MainPagerFragment

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _context = getContext();
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_vote, container, false);
        ListView lsvMoods = rootView.findViewById(R.id.lsv_moods);
        _voteListAdapter = new VoteListAdapter(getContext());
        lsvMoods.setAdapter(_voteListAdapter);
        lsvMoods.getLayoutParams().width = Utils.getWidestView(getContext(), _voteListAdapter);
        lsvMoods.setOnItemClickListener(new OnMoodItemClickListener());

        LinearLayout layVote = rootView.findViewById(R.id.lay_vote);
        layVote.setOnTouchListener(new OnFragmentTouchListener());

        return rootView;
    }

    public void setFragmentListener(VoteFragmentListener listener){
        _fragmentListener = listener;
    }

    /* LISTENERS */

    private class OnMoodItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Mood mood = _voteListAdapter.getItem(position);
            Date nowDate = new Date();
            if (!Utils.votedToday(_context, nowDate)) {
                Utils.addMoodVote(_context, mood, nowDate, false);
                _fragmentListener.onMoodItemClicked();
            }
            else{
                showDialogConfirmReselection(mood, nowDate);
            }
        }
    }

    private class OnFragmentTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                _fragmentListener.onVoteLayoutTouched();
            }
            return true;
        }
    }

    /* HELPERS */

    void showDialogConfirmReselection(final Mood mood, final Date nowDate) {
        AlertDialog.Builder dlgAlertBuilder = new AlertDialog.Builder(_context);
        dlgAlertBuilder.setMessage(getResources().getString(R.string.confirm_vote_reselect));
        dlgAlertBuilder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Utils.addMoodVote(_context, mood, nowDate, true);
                _fragmentListener.onMoodItemClicked();
            }
        });
        dlgAlertBuilder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });
        AlertDialog dlgAlert = dlgAlertBuilder.create();
        dlgAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlgAlert.show();
    }

}
