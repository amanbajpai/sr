package com.matrix.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import com.matrix.BaseActivity;
import com.matrix.R;
import com.matrix.fragment.MainMenuFragment;

public class BaseSlidingMenuActivity extends BaseActivity {
    //private final static String TAG = BaseSlidingMenuActivity.class.getSimpleName();

    private DrawerLayout mDrawerLayout;
    private FrameLayout leftDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private MainMenuFragment mainMenuFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base_sliding_menu);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftDrawer = (FrameLayout) findViewById(R.id.left_drawer);

        mainMenuFragment = new MainMenuFragment();
        FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
        t.replace(R.id.left_drawer, mainMenuFragment).commit();

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

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
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void togleMenu() {
        if (mDrawerLayout.isDrawerOpen(leftDrawer)) {
            mDrawerLayout.closeDrawers();
        } else {
            mDrawerLayout.openDrawer(leftDrawer);
        }
    }
}
