package com.vismus.appy;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vismus.appy.db.MoodVotesDatabase;
import com.vismus.appy.db.MoodVote;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.Gravity.CENTER;
import static java.lang.Character.toUpperCase;

public class AverageListAdapter extends BaseAdapter {

    enum AverageInfoDisplay {
        AVERAGE_DISPLAY_ICON,
        AVERAGE_DISPLAY_TEXT
    }

    Context _context;
    TimeUtils _timeUtils;
    Map<Integer, Pair<AveragePeriod, AverageInfo>> _itemPosToAverageInfo; // item's position -> <average period, average info>
    Map<Integer, FrameLayout> _itemPosToLayout; // item's position -> frame layout

    public AverageListAdapter(Context context) {
        _context = context;
        _timeUtils = TimeUtils.getInstance();
        _itemPosToAverageInfo = new HashMap<>();
        _itemPosToLayout = new HashMap<>();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        AveragePeriod averagePeriod = _itemPosToAverageInfo.get(position).first;
        AverageInfo averageInfo = _itemPosToAverageInfo.get(position).second;
        View itemView = new AveragesListItem(_context, averagePeriod, averageInfo);
        FrameLayout layAverageInfo = itemView.findViewById(R.id.lay_average_info);
        layAverageInfo.setOnClickListener(new OnAveragesListItemClickListener());
        layAverageInfo.setTag(position);
        _itemPosToLayout.put(position, layAverageInfo);
        return itemView;
    }

    @Override
    public int getCount() {
        return _itemPosToAverageInfo.size();
    }

    @Override
    public AverageInfo getItem(int position) {
        return _itemPosToAverageInfo.get(position).second;
    }

    public long getItemId(int position) {
        return position;
    }

    public void setItems(Map<Integer, Pair<AveragePeriod, AverageInfo>> items) {
        _itemPosToAverageInfo = items;
    }

    /* LISTENERS */

    class OnAveragesListItemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            FrameLayout layItem = (FrameLayout) view;
            int position = (int) layItem.getTag();
            AveragePeriod averagePeriod = _itemPosToAverageInfo.get(position).first;
            if (isAveragePeriodAvailable(averagePeriod)) {
                onAvailablePeriodItemClicked(position);
            } else {
                onUnavailablePeriodItemClicked(position);
            }
        }
    }

    /* EVENT HANDLERS */

    void onAvailablePeriodItemClicked(int position) {
        AverageInfo clickedItemAverageInfo = _itemPosToAverageInfo.get(position).second;
        if (clickedItemAverageInfo != null) {
            for (int i : _itemPosToLayout.keySet()) {
                AverageInfo averageInfo = _itemPosToAverageInfo.get(i).second;
                if (averageInfo != null) {
                    FrameLayout layItem = _itemPosToLayout.get(i);
                    LinearLayout layAverageInfo = (LinearLayout) layItem.getChildAt(0);
                    if (i == position) {
                        if (layAverageInfo.getTag() == AverageInfoDisplay.AVERAGE_DISPLAY_ICON) { // replace icon with text
                            setAverageText(layItem, clickedItemAverageInfo);
                        }
                        else {
                            setAverageIcon(layItem, clickedItemAverageInfo); // replace text with icon
                        }
                    }
                    else if (layAverageInfo.getTag() == AverageInfoDisplay.AVERAGE_DISPLAY_TEXT) { // set others with icon
                        setAverageIcon(layItem, averageInfo);
                    }
                }
            }
        }
        else {
            AveragePeriod averagePeriod = _itemPosToAverageInfo.get(position).first;
            showMessageAverageInfoUnavailable(averagePeriod);
        }
    }

    void onUnavailablePeriodItemClicked(int position) {
        AveragePeriod averagePeriod = _itemPosToAverageInfo.get(position).first;
        List<MoodVote> moodVotes = MoodVotesDatabase.getInstance(_context).moodVoteDao().getAll();
        Date nowDate = new Date();
        Date firstVoteDate = (moodVotes.size() != 0) ? moodVotes.get(0).getDate() : null;
        int numDaysSinceFirstVote = (firstVoteDate != null) ? _timeUtils.getDaysDiff(firstVoteDate, nowDate) + 1 : -1;
        int numDayUntilPeriodAvailable = averagePeriod.numDays() - numDaysSinceFirstVote;
        showMessageAveragePeriodUnavailable(numDayUntilPeriodAvailable, numDaysSinceFirstVote);
    }

    /* HELPERS */

    boolean isAveragePeriodAvailable(AveragePeriod averagePeriod) {
        if (averagePeriod == AveragePeriod.TODAY || averagePeriod == AveragePeriod.ALL) {
            return true;
        }
        List<MoodVote> moodVotes = MoodVotesDatabase.getInstance(_context).moodVoteDao().getAll();
        if (moodVotes.size() == 0) {
            return false;
        }
        Date nowDate = new Date();
        Date firstVoteDate = moodVotes.get(0).getDate();
        return _timeUtils.getDaysDiff(firstVoteDate, nowDate) + 1 >= averagePeriod.numDays();
    }

    void setAverageIcon(FrameLayout layItem, AverageInfo averageInfo) {
        LinearLayout layAverageInfoNew = new LinearLayout(_context);
        layAverageInfoNew.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, CENTER));
        layAverageInfoNew = Utils.getMixedMoodIcon(_context, averageInfo.getAverage());
        layAverageInfoNew.setTag(AverageInfoDisplay.AVERAGE_DISPLAY_ICON);
        layItem.removeAllViews();
        layItem.addView(layAverageInfoNew);
    }

    void setAverageText(FrameLayout layItem, AverageInfo averageInfo) {
        LinearLayout layAverageInfoNew = new LinearLayout(_context);
        layAverageInfoNew.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, CENTER));
        TextView txvAverage = new TextView(_context);
        txvAverage.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, CENTER));
        int itemPos = (int) layItem.getTag();
        if (itemPos == 0) { // period == TODAY
            txvAverage.setText(String.format("%d", (int) averageInfo.getAverage()));
            txvAverage.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            txvAverage.setText(String.format("%.2f", averageInfo.getAverage()));
        }
        txvAverage.setTextSize(15);
        txvAverage.setTextColor(Color.WHITE);
        layAverageInfoNew.addView(txvAverage);
        layAverageInfoNew.setTag(AverageInfoDisplay.AVERAGE_DISPLAY_TEXT);
        layItem.removeAllViews();
        layItem.addView(layAverageInfoNew);
    }

    void showMessageAverageInfoUnavailable(AveragePeriod averagePeriod) {
        String message;
        Resources resources = _context.getResources();
        if (averagePeriod == AveragePeriod.TODAY) {
            message = resources.getString(R.string.no_vote_today);
        } else if (averagePeriod == AveragePeriod.ALL) {
            message = resources.getString(R.string.no_vote_ever);
        } else {
            String messageFmt = resources.getString(R.string.no_vote_in_period);
            message = String.format(messageFmt, averagePeriod.title());
        }
        Toast.makeText(_context, message, Toast.LENGTH_SHORT).show();
    }

    void showMessageAveragePeriodUnavailable(int numDaysUntilAvailable, int numDaysSinceFirstVote) {
        Resources resources = _context.getResources();
        String message = resources.getString(R.string.this_category) + " ";
        if(numDaysSinceFirstVote != -1) {
            message += resources.getString(R.string.will_be_available) + " ";
            if (numDaysUntilAvailable == 1) {
                message += resources.getString(R.string.tomorrow);
            } else {
                message += resources.getString(R.string.in) + " ";
                if (numDaysUntilAvailable == 2) {
                    message += resources.getString(R.string.two_days);
                } else {
                    message += numDaysUntilAvailable + " " + resources.getString(R.string.days);
                }
            }
        }
        else {
            message += _context.getResources().getString(R.string.is_not_available);
        }
        Toast.makeText(_context, message, Toast.LENGTH_SHORT).show();
    }

    /* INNER CLASS */
    private class AveragesListItem extends LinearLayout {

        Context _context;

        // views
        PointingTextView _ptvAveragePeriod;
        FrameLayout _layAverageInfo;
        ProgressBar _prgSamplingPercentage;

        AveragesListItem(Context context, AveragePeriod averagePeriod, AverageInfo averageInfo) {
            super(context);
            View rootView = LayoutInflater.from(context).inflate(R.layout.average_list_item, this);
            _context = context;
            findViews(rootView);
            initViews(averagePeriod, averageInfo);
        }

        void findViews(View rootView){
            _ptvAveragePeriod = rootView.findViewById(R.id.ptv_average_period);
            _layAverageInfo = rootView.findViewById(R.id.lay_average_info);
            _prgSamplingPercentage = rootView.findViewById(R.id.prg_sampling_percentage);
        }

        void initViews(AveragePeriod averagePeriod, AverageInfo averageInfo) {
            _ptvAveragePeriod.setText(Utils.firstCharToUpperCase(averagePeriod.title()));
            if(isAveragePeriodAvailable(averagePeriod)){
                _ptvAveragePeriod.setViewColor(PointingTextView.ViewColor.BLUE);
                if(averageInfo != null) {
                    _layAverageInfo.setBackgroundResource(R.drawable.bg_average_info_available);
                    _layAverageInfo.addView(Utils.getMixedMoodIcon(_context, averageInfo.getAverage()));
                    if (averagePeriod == AveragePeriod.TODAY) {
                        _prgSamplingPercentage.setVisibility(GONE);
                    } else {
                        int samplingPercentage = (int) (100 * averageInfo.getSamplingFraction());
                        _prgSamplingPercentage.setProgress(Math.max(samplingPercentage, 5));
                    }
                }
                else{
                    _layAverageInfo.setBackgroundResource(R.drawable.ic_average_info_unavailable);
                    _prgSamplingPercentage.setVisibility(GONE);
                }
            }
            else{
                _ptvAveragePeriod.setViewColor(PointingTextView.ViewColor.GRAY);
                ImageView imvAveragePeriodUnavailable = new ImageView(_context);
                imvAveragePeriodUnavailable.setImageResource(R.drawable.ic_average_period_unavailable);
                _layAverageInfo.addView(imvAveragePeriodUnavailable);
                _prgSamplingPercentage.setVisibility(GONE);
            }
        }

    }

}
