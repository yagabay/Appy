package com.vismus.appy.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.vismus.appy.MainPagerAdapter;
import com.vismus.appy.R;

import java.util.ArrayList;
import java.util.List;

public class MainPagerFragment extends Fragment implements VoteFragmentListener {

    Context _context;
    MainPagerFragmentListener _fragmentListener; // listener: MainActivity

    ViewPager _vpgMain;
    MainPagerAdapter _mainPagerAdapter;

    // pager items
    VoteFragment _voteFragment;
    AveragesFragment _averagesFragment;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main_pager, container, false);
        _context = getContext();

        // find views
        _vpgMain = rootView.findViewById(R.id.vpg_main);
        ImageButton btnOptions = rootView.findViewById(R.id.btn_options);

        // set adapters & listeners
        _mainPagerAdapter = new MainPagerAdapter(getActivity().getSupportFragmentManager());
        _vpgMain.setAdapter(_mainPagerAdapter);
        _mainPagerAdapter.setItems(createFragmentList());
        _mainPagerAdapter.notifyDataSetChanged();
        btnOptions.setOnClickListener(new OnOptionsButtonClickListener());

        return rootView;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        _fragmentListener = (MainPagerFragmentListener) context;
    }

    public void refreshAveragesFragment(){
        _averagesFragment.updateAveragesList();
    }

    /* LISTENERS */

    class OnOptionsButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view){
            if(_fragmentListener != null){
                _fragmentListener.onOptionsButtonClicked();
            }
        }
    }

    /* EVENT HANDLERS */

    @Override
    public void onMoodItemClicked(){
        refreshAveragesFragment();
        _vpgMain.setCurrentItem(1);
    }

    @Override
    public void onVoteLayoutTouched(){
        refreshAveragesFragment();
    }

    /* HELPERS */

    List createFragmentList(){
        List list = new ArrayList<Fragment>();

        // vote fragment
        _voteFragment = new VoteFragment();
        _voteFragment.setFragmentListener(this);
        list.add(_voteFragment);

        // averages fragment
        _averagesFragment = new AveragesFragment();
        list.add(_averagesFragment);

        return list;
    }

}
