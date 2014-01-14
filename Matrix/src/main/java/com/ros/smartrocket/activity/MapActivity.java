package com.ros.smartrocket.activity;

import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.SurveysBL;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.SurveyDbSchema;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.entity.BookTaskResponse;
import com.ros.smartrocket.db.entity.Survey;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.dialog.BookTaskSuccessDialog;
import com.ros.smartrocket.dialog.WithdrawTaskDialog;
import com.ros.smartrocket.fragment.TasksMapFragment;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

import java.util.Calendar;
import java.util.Locale;

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
        // If you don't do this a new Fragment will be added every time this method is called (Such as on orientation change)
        if(savedInstanceState == null) {

            Intent intent = getIntent();
            if (intent != null && intent.getExtras() != null) {
                Bundle bundle = new Bundle();
                bundle.putString(Keys.MAP_MODE_VIEWTYPE, Keys.MapViewMode.SURVEYTASKS.toString());
                bundle.putInt(Keys.MAP_SURVEY_ID, intent.getExtras().getInt(Keys.MAP_SURVEY_ID));

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
