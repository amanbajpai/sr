package com.ros.smartrocket.adapter;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.utils.UIUtils;

import java.util.ArrayList;
import java.util.Locale;

public class MyTaskAdapter extends BaseAdapter {
    // private static final String TAG = "MyTaskAdapter";
    private Activity activity;
    private ArrayList<Task> items = new ArrayList<Task>();
    private LayoutInflater inflater;

    public static class ViewHolder {
        private TextView name;
        private TextView description;
        private ImageView image;
        private TextView time;
        private TextView date;
        private TextView price;
        private TextView exp;
        private TextView distance;
        private LinearLayout pendingStatusLayout;
        private LinearLayout validationStatusLayout;
        private LinearLayout reDuStatusLayout;
    }

    public MyTaskAdapter(Activity activity) {
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
            convertView = inflater.inflate(R.layout.list_item_my_task, null);
            holder = new ViewHolder();

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.description = (TextView) convertView.findViewById(R.id.description);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.price = (TextView) convertView.findViewById(R.id.price);
            holder.exp = (TextView) convertView.findViewById(R.id.exp);
            holder.distance = (TextView) convertView.findViewById(R.id.distance);
            holder.pendingStatusLayout = (LinearLayout) convertView.findViewById(R.id.pendingStatusLayout);
            holder.validationStatusLayout = (LinearLayout) convertView.findViewById(R.id.validationStatusLayout);
            holder.reDuStatusLayout = (LinearLayout) convertView.findViewById(R.id.reDuStatusLayout);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Task task = items.get(position);

        holder.name.setText(task.getName());
        holder.description.setText(task.getDescription());
        holder.price.setText(Html.fromHtml(String.format(activity.getString(R.string.task_price),
                String.format(Locale.US, "%.1f", task.getPrice()))));

        //TODO Set EXP
        holder.exp.setText(Html.fromHtml(String.format(activity.getString(R.string.task_exp),
                String.format(Locale.US, "%,d", 130))));

        holder.distance.setText(Html.fromHtml(UIUtils.convertMToKm(activity, task.getDistance(), R.string.task_distance)));

        long timeInMillisecond = UIUtils.isoTimeToLong(task.getEndDateTime());

        holder.time.setText(UIUtils.longToString(timeInMillisecond, 0));
        holder.date.setText(UIUtils.longToString(timeInMillisecond, 1));

        switch (task.getStatusId()) {
            case 1:
                holder.pendingStatusLayout.setVisibility(View.GONE);
                holder.validationStatusLayout.setVisibility(View.VISIBLE);
                holder.reDuStatusLayout.setVisibility(View.GONE);
                break;
            case 2:
                holder.pendingStatusLayout.setVisibility(View.GONE);
                holder.validationStatusLayout.setVisibility(View.GONE);
                holder.reDuStatusLayout.setVisibility(View.VISIBLE);
                break;
            case 3:
                holder.pendingStatusLayout.setVisibility(View.VISIBLE);
                holder.validationStatusLayout.setVisibility(View.GONE);
                holder.reDuStatusLayout.setVisibility(View.GONE);
                break;
            default:
                holder.pendingStatusLayout.setVisibility(View.GONE);
                holder.validationStatusLayout.setVisibility(View.GONE);
                holder.reDuStatusLayout.setVisibility(View.GONE);
                break;
        }

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
