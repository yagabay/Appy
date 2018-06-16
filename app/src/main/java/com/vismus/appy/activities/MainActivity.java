// DONE

package com.vismus.appy.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.vismus.appy.R;
import com.vismus.appy.PreferenceData;
import com.vismus.appy.Utils;
import com.vismus.appy.fragments.OptionsFragment;
import com.vismus.appy.fragments.MainPagerFragment;
import com.vismus.appy.fragments.MainPagerFragmentListener;
import com.vismus.appy.fragments.OptionsFragmentListener;
import com.vismus.appy.fragments.SplashFragment;
import com.vismus.appy.fragments.SplashFragmentListener;
import com.vismus.appy.fragments.WelcomePageFragment;
import com.vismus.appy.fragments.WelcomePageFragmentListener;

public class MainActivity extends AppCompatActivity implements SplashFragmentListener,
                                                                WelcomePageFragmentListener,
                                                                MainPagerFragmentListener,
                                                                OptionsFragmentListener {

    MainPagerFragment _mainPagerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!Utils.isDeviceTimeValid(this)){
            Toast.makeText(this, "Appy: invalid device time!", Toast.LENGTH_LONG).show();
            finish();
        }
        showSplash();
    }

    /* EVENT HANDLERS */

    @Override
    public void onSplashEnded() {
        PreferenceData prefData = PreferenceData.getInstance(this);
        if(!prefData.getAppEverLaunched()){
            prefData.setAppEverLaunched(true);
            showWelcomePage();
        }
        else{
            showMainPager();
        }
    }

    @Override
    public void onWelcomePageClosed(){
        showMainPager();
    }

    @Override
    public void onOptionsButtonClicked(){
        showOptions();
    }

    @Override
    public void onAppDataReset() {
        _mainPagerFragment.refreshAveragesFragment();
    }

    /* HELPERS */

    void showSplash(){
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.lay_main_activity, new SplashFragment())
                .commit();
    }

    void showWelcomePage(){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.lay_main_activity, new WelcomePageFragment())
                .commit();
    }

    void showMainPager(){
        _mainPagerFragment = new MainPagerFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.lay_main_activity, _mainPagerFragment)
                .commit();
    }

    void showOptions(){
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .add(R.id.lay_main_activity, new OptionsFragment())
                .commit();
    }

}
