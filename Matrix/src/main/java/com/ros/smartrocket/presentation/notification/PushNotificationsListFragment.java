package com.ros.smartrocket.presentation.notification;

import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.NotificationDbSchema;
import com.ros.smartrocket.db.bl.NotificationBL;
import com.ros.smartrocket.db.entity.Notification;
import com.ros.smartrocket.presentation.base.BaseFragment;
import com.ros.smartrocket.ui.adapter.NotificationAdapter;
import com.ros.smartrocket.utils.PreferencesManager;

import java.util.ArrayList;

public class PushNotificationsListFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private ArrayList<Notification> notifications;
    private ListView notificationsListView;
    private DbHandler handler;
    private NotificationAdapter adapter;
    private PushReceiver localReceiver;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        return localInflater.inflate(R.layout.fragment_push_notifications, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handler = new DbHandler(getActivity().getContentResolver());

        notificationsListView = (ListView) view.findViewById(R.id.notificationsList);

        notifications = new ArrayList<>();
        adapter = new NotificationAdapter(getActivity(), notifications);
        notificationsListView.setAdapter(adapter);
        notificationsListView.setOnItemClickListener(this);

        NotificationBL.getNotificationsFromDB(handler);

        localReceiver = new PushReceiver();
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Keys.REFRESH_PUSH_NOTIFICATION_LIST);
        getActivity().registerReceiver(localReceiver, intentFilter);
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(localReceiver);
        super.onStop();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(android.R.id.content, PushNotificationFragment.getInstance(notifications.get(i)
                .get_id()));
        fragmentTransaction.addToBackStack(PushNotificationFragment.class.getSimpleName());
        fragmentTransaction.commit();

    }

    class DbHandler extends AsyncQueryHandler {
        public DbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (token == NotificationDbSchema.Query.TOKEN_QUERY) {
                notifications.clear();
                notifications.addAll(NotificationBL.convertCursorToNotificationList(cursor));
                adapter.notifyDataSetChanged();
            }
        }
    }

    public class PushReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (Keys.REFRESH_PUSH_NOTIFICATION_LIST.equals(action)) {
                NotificationBL.getNotificationsFromDB(handler);
            }

            PreferencesManager.getInstance().setShowPushNotifStar(false);
        }
    }
}
