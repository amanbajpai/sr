package com.ros.smartrocket.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.NotificationBL;
import com.ros.smartrocket.db.entity.Notification;

import java.util.List;

/**
 * Created by macbook on 08.10.15.
 */
public class PushNotificationsListFragment extends Fragment {

    private List<Notification> notifications;
    private ListView notificationsListView;


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

        notificationsListView = (ListView) view.findViewById(R.id.notificationsList);

        notifications = NotificationBL.createFakeNotifications();



    }
}
