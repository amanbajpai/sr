package com.ros.smartrocket.adapter;

import android.app.Activity;
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
import java.util.Calendar;
import java.util.Locale;

public class MyTaskAdapter extends BaseAdapter {
    // private static final String TAG = "MyTaskAdapter";
    private Activity activity;
    private ArrayList<Task> items = new ArrayList<Task>();
    private LayoutInflater inflater;
    private Calendar calendar = Calendar.getInstance();

    public static class ViewHolder {
        private LinearLayout listItem;

        private TextView name;
        private ImageView arrow;
        private ImageView image;
        private TextView timeLeft;
        private TextView distance;

        private TextView statusText;
        private LinearLayout optionLayout;
        private View optionDivider;

        private TextView taskPrice;
        private TextView taskExp;
        private TextView textQuestionsCount;
        private TextView photoQuestionsCount;
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

            holder.listItem = (LinearLayout) convertView.findViewById(R.id.listItem);

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.arrow = (ImageView) convertView.findViewById(R.id.arrow);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.timeLeft = (TextView) convertView.findViewById(R.id.timeLeft);
            holder.distance = (TextView) convertView.findViewById(R.id.distance);

            holder.statusText = (TextView) convertView.findViewById(R.id.statusText);
            holder.optionLayout = (LinearLayout) convertView.findViewById(R.id.optionLayout);
            holder.optionDivider = (View) convertView.findViewById(R.id.optionDivider);

            holder.taskPrice = (TextView) convertView.findViewById(R.id.taskPrice);
            holder.taskExp = (TextView) convertView.findViewById(R.id.taskExp);
            holder.textQuestionsCount = (TextView) convertView.findViewById(R.id.textQuestionsCount);
            holder.photoQuestionsCount = (TextView) convertView.findViewById(R.id.photoQuestionsCount);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Task task = items.get(position);

        holder.name.setText(task.getName());
        holder.taskPrice.setText(activity.getString(R.string.hk) + String.format(Locale.US, "%.0f", task.getPrice()));
        holder.taskExp.setText(String.format(Locale.US, "%.0f", task.getExperienceOffer()));
        holder.textQuestionsCount.setText("0");
        holder.photoQuestionsCount.setText("0");

        //long startTimeInMillisecond = UIUtils.isoTimeToLong(task.getStartDateTime());
        long endTimeInMillisecond = UIUtils.isoTimeToLong(task.getEndDateTime());
        long redoTillTimeInMillisecond = UIUtils.isoTimeToLong(task.getRemakeTill());
        long leftTimeInMillisecond = endTimeInMillisecond - calendar.getTimeInMillis();

        holder.timeLeft.setText(UIUtils.getTimeInDayHoursMinutes(activity, leftTimeInMillisecond));
        holder.distance.setText(UIUtils.convertMToKm(activity, task.getDistance(), R.string.m_to_km_with_text_mask, true));

        switch (TasksBL.getTaskStatusType(task.getStatusId())) {
            case claimed:
            case started:
            case scheduled:
                holder.arrow.setVisibility(View.VISIBLE);
                holder.listItem.setBackgroundResource(R.drawable.mission_green_bg);
                holder.statusText.setBackgroundColor(activity.getResources().getColor(R.color.grey_light));
                holder.statusText.setTextColor(activity.getResources().getColor(R.color.grey));
                holder.statusText.setText(activity.getString(R.string.mission_expires_at,
                        UIUtils.longToString(endTimeInMillisecond, 3)));

                holder.optionLayout.setBackgroundColor(activity.getResources().getColor(R.color.green));
                holder.optionDivider.setBackgroundColor(activity.getResources().getColor(R.color.green_light));

                holder.taskPrice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_green, 0, 0, 0);
                holder.taskExp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket_green, 0, 0, 0);
                holder.textQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.quote_green, 0, 0, 0);
                holder.photoQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera_green, 0, 0, 0);
                break;
            case validation:
                holder.arrow.setVisibility(View.GONE);
                holder.listItem.setBackgroundResource(R.drawable.mission_grey_bg);
                holder.statusText.setBackgroundColor(activity.getResources().getColor(R.color.grey_light));
                holder.statusText.setTextColor(activity.getResources().getColor(R.color.grey));
                holder.statusText.setText(activity.getString(R.string.mission_in_validation));

                holder.optionLayout.setBackgroundColor(activity.getResources().getColor(R.color.grey));
                holder.optionDivider.setBackgroundColor(activity.getResources().getColor(R.color.grey_light));

                holder.taskPrice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_lightgrey, 0, 0, 0);
                holder.taskExp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket_lightgrey, 0, 0, 0);
                holder.textQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.quote_lightgrey, 0, 0, 0);
                holder.photoQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera_lightgrey, 0, 0, 0);
                break;
            case reDoTask:
                holder.arrow.setVisibility(View.VISIBLE);
                holder.listItem.setBackgroundResource(R.drawable.mission_red_bg);
                holder.statusText.setBackgroundColor(activity.getResources().getColor(R.color.red_light));
                holder.statusText.setTextColor(activity.getResources().getColor(R.color.white));
                holder.statusText.setText(activity.getString(R.string.redo_mission_till,
                        UIUtils.longToString(redoTillTimeInMillisecond, 3)));

                holder.optionLayout.setBackgroundColor(activity.getResources().getColor(R.color.red));
                holder.optionDivider.setBackgroundColor(activity.getResources().getColor(R.color.red_light));

                holder.taskPrice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_red, 0, 0, 0);
                holder.taskExp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket_red, 0, 0, 0);
                holder.textQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.quote_lightgrey, 0, 0,
                        0); //TODO
                holder.photoQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera_lightgrey, 0, 0,
                        0); //TODO
                break;
            case pending:
                holder.arrow.setVisibility(View.GONE);
                holder.listItem.setBackgroundResource(R.drawable.mission_blue_bg);
                holder.statusText.setBackgroundColor(activity.getResources().getColor(R.color.blue_light));
                holder.statusText.setTextColor(activity.getResources().getColor(R.color.white));
                holder.statusText.setText(activity.getString(R.string.pending));

                holder.optionLayout.setBackgroundColor(activity.getResources().getColor(R.color.blue));
                holder.optionDivider.setBackgroundColor(activity.getResources().getColor(R.color.blue_light));

                holder.taskPrice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_blue, 0, 0, 0);
                holder.taskExp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket_blue, 0, 0, 0);
                holder.textQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.quote_lightgrey, 0, 0,
                        0); //TODO
                holder.photoQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera_lightgrey, 0, 0,
                        0); //TODO
                break;
            case completed:
                holder.arrow.setVisibility(View.GONE);
                holder.listItem.setBackgroundResource(R.drawable.mission_gold_bg);
                holder.statusText.setBackgroundColor(activity.getResources().getColor(R.color.yellow));
                holder.statusText.setTextColor(activity.getResources().getColor(R.color.white));
                holder.statusText.setText(activity.getString(R.string.mission_completed));

                holder.optionLayout.setBackgroundColor(activity.getResources().getColor(R.color.orange));
                holder.optionDivider.setBackgroundColor(activity.getResources().getColor(R.color.yellow));

                holder.taskPrice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_gold, 0, 0, 0);
                holder.taskExp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket_gold, 0, 0, 0);
                holder.textQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.quote_gold, 0, 0, 0);
                holder.photoQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera_gold, 0, 0, 0);
                break;
            case validated:
                holder.arrow.setVisibility(View.GONE);
                holder.listItem.setBackgroundResource(R.drawable.mission_gold_bg);
                holder.statusText.setBackgroundColor(activity.getResources().getColor(R.color.yellow));
                holder.statusText.setTextColor(activity.getResources().getColor(R.color.white));
                holder.statusText.setText(activity.getString(R.string.mission_validated));

                holder.optionLayout.setBackgroundColor(activity.getResources().getColor(R.color.orange));
                holder.optionDivider.setBackgroundColor(activity.getResources().getColor(R.color.yellow));

                holder.taskPrice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_gold, 0, 0, 0);
                holder.taskExp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket_gold, 0, 0, 0);
                holder.textQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.quote_gold, 0, 0, 0);
                holder.photoQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera_gold, 0, 0, 0);
                break;
            case rejected:
                holder.arrow.setVisibility(View.GONE);
                holder.listItem.setBackgroundResource(R.drawable.mission_dark_bg);
                holder.statusText.setBackgroundColor(activity.getResources().getColor(R.color.grey_dark));
                holder.statusText.setTextColor(activity.getResources().getColor(R.color.white));
                holder.statusText.setText(activity.getString(R.string.mission_in_validation));

                holder.optionLayout.setBackgroundColor(activity.getResources().getColor(R.color.black_light));
                holder.optionDivider.setBackgroundColor(activity.getResources().getColor(R.color.grey_dark));

                holder.taskPrice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_grey, 0, 0, 0);
                holder.taskExp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket_grey, 0, 0, 0);
                holder.textQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.quote_grey, 0, 0, 0);
                holder.photoQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera_grey, 0, 0, 0);
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
