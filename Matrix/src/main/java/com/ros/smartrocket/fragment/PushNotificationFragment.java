package com.ros.smartrocket.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.*;
import android.widget.TextView;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.NotificationBL;
import com.ros.smartrocket.db.NotificationDbSchema;
import com.ros.smartrocket.db.entity.Notification;
import com.ros.smartrocket.utils.TimeUtils;

import java.util.List;

/**
 * Created by macbook on 08.10.15.
 */
public class PushNotificationFragment extends Fragment {

    private static final String ID = "id";

    private DbHandler handler;
    private TextView subject, time, text;
    private long notificationId;

    @Deprecated
    public PushNotificationFragment() {
    }

    public static PushNotificationFragment getInstance(long id) {
        Bundle b = new Bundle();
        b.putLong(ID, id);
        PushNotificationFragment fragment = new PushNotificationFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        return localInflater.inflate(R.layout.fragment_push_notification, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        text = (TextView) view.findViewById(R.id.text);
        text.setMovementMethod(LinkMovementMethod.getInstance());

        subject = (TextView) view.findViewById(R.id.subject);
        time = (TextView) view.findViewById(R.id.time);

        handler = new DbHandler(getActivity().getContentResolver());

        notificationId = getArguments().getLong(ID);
        NotificationBL.getNotificationFromDB(handler, notificationId);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_delete, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete) {
            NotificationBL.deleteNotification(getActivity().getContentResolver(), notificationId);
            getActivity().onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUI(Notification notification) {

        subject.setText(notification.getSubject());
        text.setText(Html.fromHtml(notification.getMessage()));
        time.setText(TimeUtils.getFormattedTimestamp(notification.getTimestamp()));

        if (!notification.getRead()) {
            notification.setRead(true);
            NotificationBL.updateNotification(getActivity().getContentResolver(), notification);
        }
    }

    class DbHandler extends AsyncQueryHandler {
        public DbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case NotificationDbSchema.Query.TOKEN_QUERY:
                    List<Notification> notifications = NotificationBL.convertCursorToNotificationList(cursor);
                    updateUI(notifications.get(0));
                    break;
                default:
                    break;
            }
        }
    }

}
