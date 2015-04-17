package com.vicmns.demogmaillikedrawer.lib;

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by vicmns on 2/12/2015.
 */
public class ActionBarSlidingPanelToggle extends ActionBarDrawerToggle {
    private SlidingPaneLayout mSlidingPanelLayout;

    public ActionBarSlidingPanelToggle(Activity activity, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
        super(activity, new DrawerLayout(activity), openDrawerContentDescRes, closeDrawerContentDescRes);
    }

    public SlidingPaneLayout getDrawerLayout() {
        return mSlidingPanelLayout;
    }

    public void setDrawerLayout(SlidingPaneLayout drawerLayout) {
        mSlidingPanelLayout = drawerLayout;
    }

    @Override
    public void setToolbarNavigationClickListener(View.OnClickListener onToolbarNavigationClickListener) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mSlidingPanelLayout.isOpen()) {
                    mSlidingPanelLayout.closePane();
                } else {
                    mSlidingPanelLayout.openPane();
                }
                return true;
        }

        return false;
    }
}
