package com.ros.smartrocket.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Notification;
import com.ros.smartrocket.utils.TimeUtils;

import java.util.ArrayList;

/**
 * Created by macbook on 09.10.15.
 */
public class NotificationAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private ArrayList<Notification> notifications;
    private Resources resources;

    public NotificationAdapter(Context context, ArrayList<Notification> notifications) {
        resources = context.getResources();
        layoutInflater = LayoutInflater.from(context);
        this.notifications = notifications;
    }

    @Override
    public int getCount() {
        return notifications.size();
    }

    @Override
    public Object getItem(int i) {
        return notifications.get(i);
    }

    @Override
    public long getItemId(int i) {
        return notifications.get(i).get_id();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = layoutInflater.inflate(R.layout.list_item_notification, null);

//            viewHolder.imageView = (ImageView) view.findViewById(R.id)
            viewHolder.subject = (TextView) view.findViewById(R.id.subject);
            viewHolder.text = (TextView) view.findViewById(R.id.text);
            viewHolder.timestamp = (TextView) view.findViewById(R.id.time);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (notifications.get(i).getRead()) {
            view.setBackgroundColor(resources.getColor(R.color.white));
        } else {
            view.setBackgroundColor(resources.getColor(R.color.notification_unread));
        }

        viewHolder.subject.setText(notifications.get(i).getSubject());
        viewHolder.text.setText(Html.fromHtml(notifications.get(i).getMessage()));
        viewHolder.timestamp.setText(TimeUtils.getFormattedTimestamp(notifications.get(i).getTimestamp()));

        return view;
    }

    private class ViewHolder {
        //        ImageView imageView;
        TextView subject, text, timestamp;
    }
}
