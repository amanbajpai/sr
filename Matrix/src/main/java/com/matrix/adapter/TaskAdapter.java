package com.matrix.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.matrix.R;
import com.matrix.db.entity.Task;

import java.util.ArrayList;

public class TaskAdapter extends BaseAdapter {
    // private static final String TAG = "NewFeedAdapter";
    private Activity activity;
    private ArrayList<Task> items = new ArrayList<Task>();
    private LayoutInflater inflater;

    public static class ViewHolder {
        TextView name;
        TextView description;
    }

    public TaskAdapter(Activity activity) {
        this.activity = activity;

        inflater = LayoutInflater.from(activity);
    }

    public int getCount() {
        return items.size();
    }

    public Task getItem(int position) {
        return items.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    
    public void setData(final ArrayList<Task> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void addData(final ArrayList<Task> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_task, null);
            holder = new ViewHolder();

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.description = (TextView) convertView.findViewById(R.id.description);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Task task = items.get(position);

        holder.name.setText(task.getName());
        holder.description.setText(task.getDescription());

        return convertView;
    }

    public long getLastItemId() {
        if (items.size() > 0) {
            return items.get(items.size() - 1).get_id();
        } else {
            return 0;
        }
    }
}