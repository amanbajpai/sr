package com.ros.smartrocket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Notification;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macbook on 09.10.15.
 */
public class NotificationAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private ArrayList<Notification> notifications;

    public NotificationAdapter(Context context, ArrayList<Notification> notifications){
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
        return notifications.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        if (view == null){
            view = layoutInflater.inflate(R.layout.list_item_notification, null);

//            viewHolder.
        }


        return view;
    }

    private class ViewHolder{
        ImageView imageView;
        TextView textView;
    }
}
