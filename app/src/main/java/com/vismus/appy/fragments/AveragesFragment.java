package com.vismus.appy.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.vismus.appy.AverageInfo;
import com.vismus.appy.AveragePeriod;
import com.vismus.appy.AverageListAdapter;
import com.vismus.appy.R;
import com.vismus.appy.TimeUtils;
import com.vismus.appy.db.MoodVotesDatabase;
import com.vismus.appy.db.MoodVote;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class AveragesFragment extends Fragment {

    ListView _lsvAverages;
    AverageListAdapter _averagesListAdapter;
    TimeUtils _timeUtils;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_averages, container, false);
        _timeUtils = TimeUtils.getInstance();
        _lsvAverages = rootView.findViewById(R.id.lsv_averages);
        _averagesListAdapter = new AverageListAdapter(getContext());
        _averagesListAdapter.setItems(createAveragesList());
        _lsvAverages.setAdapter(_averagesListAdapter);
        AveragePeriod.setContext(getContext());
        return rootView;
    }

    public void updateAveragesList(){
        _averagesListAdapter.setItems(createAveragesList());
        _averagesListAdapter.notifyDataSetChanged();
    }

    /* HELPERS */

    Map<Integer, Pair<AveragePeriod, AverageInfo>> createAveragesList(){
        Map<Integer, Pair<AveragePeriod, AverageInfo>> averagesList = new HashMap<>();
        List<MoodVote> votes = MoodVotesDatabase.getInstance(getContext()).moodVoteDao().getAll();

        Date nowDate = new Date();

        // period = TODAY
        averagesList.put(0, new Pair(AveragePeriod.TODAY, calcAverageInfoPerPeriod(votes, AveragePeriod.TODAY, nowDate)));

        AveragePeriod period = AveragePeriod.LAST_THREE_DAYS;

        // available periods != TODAY, ALL
        MoodVote firstVote = (votes.size() != 0) ? votes.get(0) : null;
        if(firstVote != null) {
            int numDaysSinceFirstVote = _timeUtils.getDaysDiff(firstVote.getDate(), nowDate) + 1;
            while (period.numDays() <= numDaysSinceFirstVote) {
                averagesList.put(period.ordinal(), new Pair(period, calcAverageInfoPerPeriod(votes, period, nowDate)));
                period = period.next();
            }
        }

        // period = ALL
        averagesList.put(period.ordinal(), new Pair(AveragePeriod.ALL, calcAverageInfoPerPeriod(votes, AveragePeriod.ALL, nowDate)));

        // unavailable periods != TODAY, ALL
        while(period.ordinal() + 1 < AveragePeriod.values().length) {
            averagesList.put(period.ordinal() + 1, new Pair(period, null));
            period = period.next();
        }

        return averagesList;
    }


    AverageInfo calcAverageInfoPerPeriod(List<MoodVote> votes, AveragePeriod period, Date nowDate){
        if(votes.size() == 0){
            return null;
        }
        int countSamples = 0;
        int sumScores = 0;
        ListIterator itVotes = votes.listIterator(votes.size());
        MoodVote vote = (MoodVote) itVotes.previous();
        while(vote != null){
            int numDaysSinceVote = _timeUtils.getDaysDiff(vote.getDate(), nowDate) + 1;
            if(numDaysSinceVote > period.numDays()) {
                break;
            }
            ++countSamples;
            sumScores += vote.getMood().score();
            vote = itVotes.hasPrevious() ? (MoodVote) itVotes.previous() : null;
        }
        if(countSamples == 0) {
            return null;
        }
        int numDays = (period != AveragePeriod.ALL) ? period.numDays() : _timeUtils.getDaysDiff(votes.get(0).getDate(), nowDate) + 1;
        double average = (double) sumScores / countSamples;
        double samplingFraction = (double) countSamples / numDays;
        return new AverageInfo(average, samplingFraction);
    }

}

