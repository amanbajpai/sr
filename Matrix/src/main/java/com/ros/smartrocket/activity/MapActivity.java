package com.ros.smartrocket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.fragment.TasksMapFragment;

/**
 * Activity for view Task detail information
 */
public class MapActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // If not already added to the Fragment manager add it.
        // If you don't do this a new Fragment will be added every time this method is  called (Such as on
        // orientation change)
        if (savedInstanceState == null) {

            Intent intent = getIntent();
            if (intent != null && intent.getExtras() != null) {
                Bundle bundle = new Bundle();
                bundle.putString(Keys.MAP_MODE_VIEWTYPE, intent.getExtras().getString(Keys.MAP_MODE_VIEWTYPE));
                bundle.putInt(Keys.MAP_VIEW_ITEM_ID, intent.getExtras().getInt(Keys.MAP_VIEW_ITEM_ID));

                Fragment fragment = new TasksMapFragment();
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragment).commit();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_custom_view_simple_text);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        View view = actionBar.getCustomView();
        ((TextView) view.findViewById(R.id.titleTextView)).setText(R.string.task_location_title);

        return true;
    }
}
