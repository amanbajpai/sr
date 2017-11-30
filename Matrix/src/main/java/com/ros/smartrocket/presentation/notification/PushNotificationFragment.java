package com.ros.smartrocket.presentation.notification;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.db.NotificationDbSchema;
import com.ros.smartrocket.db.bl.NotificationBL;
import com.ros.smartrocket.db.entity.Notification;
import com.ros.smartrocket.presentation.base.BaseFragment;
import com.ros.smartrocket.utils.TimeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PushNotificationFragment extends BaseFragment {

    private static final String ID = "id";
    @BindView(R.id.subject)
    TextView subject;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.text)
    TextView text;
    Unbinder unbinder;

    private DbHandler handler;
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
        View view = localInflater.inflate(R.layout.fragment_push_notification, null);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        text.setMovementMethod(LinkMovementMethod.getInstance());
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
