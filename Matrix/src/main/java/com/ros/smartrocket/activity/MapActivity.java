package com.ros.smartrocket.activity;

import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.fragment.TasksMapFragment;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.utils.PreferencesManager;

/**
 * Activity for view Task detail information
 */
public class MapActivity extends BaseActivity {
    private static final String TAG = MapActivity.class.getSimpleName();
    private APIFacade apiFacade = APIFacade.getInstance();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();

    private AsyncQueryHandler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.task_location_title);

        // If not already added to the Fragment manager add it.
        // If you don't do this a new Fragment will be added every time this method is  called (Such as on
        // orientation change)
        if (savedInstanceState == null) {

            Intent intent = getIntent();
            if (intent != null && intent.getExtras() != null) {
                Bundle bundle = new Bundle();
                bundle.putString(Keys.MAP_MODE_VIEWTYPE, intent.getExtras().getString(Keys.MAP_MODE_VIEWTYPE));
                bundle.putInt(Keys.MAP_VIEWITEM_ID, intent.getExtras().getInt(Keys.MAP_VIEWITEM_ID));

                Fragment fragment = new TasksMapFragment();
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragment).commit();
            }
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                finish();
//                break;
//            default:
//                break;
//
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
