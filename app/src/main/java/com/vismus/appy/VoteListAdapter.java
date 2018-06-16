package com.vismus.appy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static com.vismus.appy.Utils.getMoodIcon;

public class VoteListAdapter extends BaseAdapter {

    private Context _context;
    private LayoutInflater _layoutInflater;
    private List<Mood> _moods;

    public VoteListAdapter(Context context){
        _context = context;
        _layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        _moods = new ArrayList<>();
        for(Mood mood : EnumSet.allOf(Mood.class)){
            _moods.add(mood);
        }
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View itemView = _layoutInflater.inflate(R.layout.vote_list_item, parent, false);
        Mood mood = _moods.get(_moods.size() - position - 1);
        FrameLayout layMoodIcon = itemView.findViewById(R.id.lay_mood_icon);
        layMoodIcon.addView(getMoodIcon(_context, mood));
        return itemView;
    }

    @Override
    public int getCount(){
        return _moods.size();
    }

    @Override
    public Mood getItem(int position)
    {
        return _moods.get(_moods.size() - position - 1);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

}
