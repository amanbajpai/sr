package com.ros.smartrocket.presentation.main;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import com.ros.smartrocket.R;
import com.ros.smartrocket.presentation.base.BaseActivity;
import com.ros.smartrocket.presentation.main.menu.MainMenuFragment;

@SuppressLint("Registered")
public class BaseSlidingMenuActivity extends BaseActivity {
    private DrawerLayout mDrawerLayout;
    private FrameLayout leftDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private MainMenuFragment mainMenuFragment;

    public BaseSlidingMenuActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_base_sliding_menu);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftDrawer = (FrameLayout) findViewById(R.id.left_drawer);

        mainMenuFragment = new MainMenuFragment();
        FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
        t.replace(R.id.left_drawer, mainMenuFragment).commit();

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.app_name,
                R.string.app_name) {
            public void onDrawerClosed(View view) {
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        setHomeAsUp();
        if (getSupportActionBar() != null) getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    public void toggleMenu() {
        if (mDrawerLayout.isDrawerOpen(leftDrawer))
            mDrawerLayout.closeDrawers();
        else
            mDrawerLayout.openDrawer(leftDrawer);
    }
}
