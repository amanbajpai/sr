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
import com.ros.smartrocket.bl.TasksBL;
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
        private LinearLayout scheduledStatusLayout;
        private LinearLayout completedStatusLayout;
        private LinearLayout claimedStatusLayout;
        private LinearLayout startedStatusLayout;
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
            holder.scheduledStatusLayout = (LinearLayout) convertView.findViewById(R.id.scheduledStatusLayout);
            holder.completedStatusLayout = (LinearLayout) convertView.findViewById(R.id.completedStatusLayout);
            holder.claimedStatusLayout = (LinearLayout) convertView.findViewById(R.id.claimedStatusLayout);
            holder.startedStatusLayout = (LinearLayout) convertView.findViewById(R.id.startedStatusLayout);

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

        holder.distance.setText(Html.fromHtml(UIUtils.convertMToKm(activity, task.getDistance(),
                R.string.task_distance)));

        long timeInMillisecond = UIUtils.isoTimeToLong(task.getEndDateTime());

        holder.time.setText(UIUtils.longToString(timeInMillisecond, 0));
        holder.date.setText(UIUtils.longToString(timeInMillisecond, 1));

        holder.pendingStatusLayout.setVisibility(View.GONE);
        holder.validationStatusLayout.setVisibility(View.GONE);
        holder.reDuStatusLayout.setVisibility(View.GONE);
        holder.scheduledStatusLayout.setVisibility(View.GONE);
        holder.completedStatusLayout.setVisibility(View.GONE);
        holder.claimedStatusLayout.setVisibility(View.GONE);
        holder.startedStatusLayout.setVisibility(View.GONE);

        switch (TasksBL.getTaskStatusType(task.getStatusId())) {
            case claimed:
                holder.claimedStatusLayout.setVisibility(View.VISIBLE);
                break;
            case started:
                holder.startedStatusLayout.setVisibility(View.VISIBLE);
                break;
            case scheduled:
                holder.scheduledStatusLayout.setVisibility(View.VISIBLE);
                break;
            case validation:
                holder.validationStatusLayout.setVisibility(View.VISIBLE);
                break;
            case reDoTask:
                holder.reDuStatusLayout.setVisibility(View.VISIBLE);
                break;
            case pending:
                holder.pendingStatusLayout.setVisibility(View.VISIBLE);
                break;
            case completed:
                holder.completedStatusLayout.setVisibility(View.VISIBLE);
                break;
            default:
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
