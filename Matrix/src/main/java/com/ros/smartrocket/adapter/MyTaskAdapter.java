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
import java.util.List;
import java.util.Locale;

public class MyTaskAdapter extends BaseAdapter {
    private Activity activity;
    private List<Task> items = new ArrayList<Task>();
    private LayoutInflater inflater;
    private Calendar calendar = Calendar.getInstance();

    public static class ViewHolder {
        private LinearLayout listItem;

        private TextView name;
        private ImageView image;
        private TextView timeLeft;
        private TextView distance;

        private LinearLayout timeAndDistanceLayout;
        private TextView locationNameAddressText;

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

    public void setData(final List<Task> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_my_task, null);
            holder = new ViewHolder();

            holder.listItem = (LinearLayout) convertView.findViewById(R.id.listItem);

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.timeLeft = (TextView) convertView.findViewById(R.id.timeLeft);
            holder.distance = (TextView) convertView.findViewById(R.id.distance);

            holder.timeAndDistanceLayout = (LinearLayout) convertView.findViewById(R.id.timeAndDistanceLayout);
            holder.locationNameAddressText = (TextView) convertView.findViewById(R.id.locationNameAddressText);

            holder.statusText = (TextView) convertView.findViewById(R.id.statusText);
            holder.optionLayout = (LinearLayout) convertView.findViewById(R.id.optionLayout);
            holder.optionDivider = convertView.findViewById(R.id.optionDivider);

            holder.taskPrice = (TextView) convertView.findViewById(R.id.taskPrice);
            holder.taskExp = (TextView) convertView.findViewById(R.id.taskExp);
            holder.textQuestionsCount = (TextView) convertView.findViewById(R.id.textQuestionsCount);
            holder.photoQuestionsCount = (TextView) convertView.findViewById(R.id.photoQuestionsCount);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Task task = items.get(position);

        holder.timeAndDistanceLayout.setVisibility(View.GONE);
        holder.locationNameAddressText.setVisibility(View.GONE);

        holder.name.setText(task.getName());
        holder.image.setImageResource(UIUtils.getWaveTypeListIcon(2));
        holder.locationNameAddressText.setText(task.getAddress());
        holder.taskPrice.setText(UIUtils.getBalanceOrPrice(activity, task.getPrice()));
        holder.taskExp.setText(String.format(Locale.US, "%.0f", task.getExperienceOffer()));
        holder.textQuestionsCount.setText("0");
        holder.photoQuestionsCount.setText("0");

        long claimTimeInMillisecond = UIUtils.isoTimeToLong(task.getClaimed());
        long timeoutInMillisecond = UIUtils.getHoursAsMilliseconds(task.getExpireTimeoutForClaimedTask());
        long missionDueMillisecond = claimTimeInMillisecond + timeoutInMillisecond;
        long dueInMillisecond = missionDueMillisecond - calendar.getTimeInMillis();
        //long leftTimeInMillisecond = timeoutInMillisecond - (calendar.getTimeInMillis() - claimTimeInMillisecond);


        holder.timeLeft.setText(UIUtils.getTimeInDayHoursMinutes(activity, dueInMillisecond)
                + " " + activity.getString(R.string.time_left));
        holder.distance.setText(UIUtils.convertMToKm(activity, task.getDistance(), R.string.m_to_km_with_text_mask, true));

        switch (TasksBL.getTaskStatusType(task.getStatusId())) {
            case none:
            case claimed:
            case started:
                holder.name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_grey_big, 0);
                holder.listItem.setBackgroundResource(R.drawable.mission_green_bg);

                holder.timeAndDistanceLayout.setVisibility(View.VISIBLE);

                holder.statusText.setBackgroundColor(activity.getResources().getColor(R.color.grey_light));
                holder.statusText.setTextColor(activity.getResources().getColor(R.color.grey));
                holder.statusText.setText(activity.getString(R.string.mission_expires_at,
                        UIUtils.longToString(missionDueMillisecond, 3)));

                holder.optionLayout.setBackgroundColor(activity.getResources().getColor(R.color.green));
                holder.optionDivider.setBackgroundColor(activity.getResources().getColor(R.color.green_light));

                holder.taskPrice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_green, 0, 0, 0);
                holder.taskExp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket_green, 0, 0, 0);
                holder.textQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.quote_green, 0, 0, 0);
                holder.photoQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera_green, 0, 0, 0);
                break;
            case scheduled:
            case pending:
                holder.name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                holder.listItem.setBackgroundResource(R.drawable.mission_blue_bg);

                holder.timeAndDistanceLayout.setVisibility(View.VISIBLE);

                holder.statusText.setBackgroundColor(activity.getResources().getColor(R.color.blue_light));
                holder.statusText.setTextColor(activity.getResources().getColor(R.color.white));
                holder.statusText.setText(activity.getString(R.string.send_latter_mission,
                        UIUtils.longToString(missionDueMillisecond, 3)));

                holder.optionLayout.setBackgroundColor(activity.getResources().getColor(R.color.blue));
                holder.optionDivider.setBackgroundColor(activity.getResources().getColor(R.color.blue_light));

                holder.taskPrice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_blue, 0, 0, 0);
                holder.taskExp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket_blue, 0, 0, 0);
                holder.textQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.quote_blue, 0, 0, 0);
                holder.photoQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera_blue, 0, 0, 0);
                break;
            case completed:
                holder.name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                holder.listItem.setBackgroundResource(R.drawable.mission_grey_bg);

                holder.locationNameAddressText.setVisibility(View.VISIBLE);

                holder.statusText.setBackgroundColor(activity.getResources().getColor(R.color.grey_light));
                holder.statusText.setTextColor(activity.getResources().getColor(R.color.grey));
                holder.statusText.setText(activity.getString(R.string.mission_in_validation));

                holder.optionLayout.setBackgroundColor(activity.getResources().getColor(R.color.grey));
                holder.optionDivider.setBackgroundColor(activity.getResources().getColor(R.color.grey_light));

                holder.taskPrice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_grey, 0, 0, 0);
                holder.taskExp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket_grey, 0, 0, 0);
                holder.textQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.quote_grey, 0, 0, 0);
                holder.photoQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera_grey, 0, 0, 0);
                break;
            case validation:
                holder.name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                holder.listItem.setBackgroundResource(R.drawable.mission_grey_bg);

                holder.locationNameAddressText.setVisibility(View.VISIBLE);

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
                long reDoTimeInMillisecond = UIUtils.isoTimeToLong(task.getRedoDate());
                long missionDueForReDoInMillisecond = reDoTimeInMillisecond + timeoutInMillisecond;
                long dueInForReDoMillisecond = missionDueForReDoInMillisecond - calendar.getTimeInMillis();

                holder.name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_grey_big, 0);
                holder.listItem.setBackgroundResource(R.drawable.mission_red_bg);

                holder.timeAndDistanceLayout.setVisibility(View.VISIBLE);

                holder.timeLeft.setText(UIUtils.getTimeInDayHoursMinutes(activity, dueInForReDoMillisecond)
                        + " " + activity.getString(R.string.time_left));

                holder.statusText.setBackgroundColor(activity.getResources().getColor(R.color.red));
                holder.statusText.setTextColor(activity.getResources().getColor(R.color.white));
                holder.statusText.setText(activity.getString(R.string.redo_mission,
                        UIUtils.longToString(missionDueForReDoInMillisecond, 3)));

                holder.optionLayout.setBackgroundColor(activity.getResources().getColor(R.color.red_dark));
                holder.optionDivider.setBackgroundColor(activity.getResources().getColor(R.color.red));

                holder.taskPrice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_red, 0, 0, 0);
                holder.taskExp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket_red, 0, 0, 0);
                holder.textQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.quote_red, 0, 0, 0);
                holder.photoQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera_red, 0, 0, 0);
                break;
            case validated:
                holder.name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                holder.listItem.setBackgroundResource(R.drawable.mission_gold_bg);

                holder.locationNameAddressText.setVisibility(View.VISIBLE);

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
                holder.name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                holder.listItem.setBackgroundResource(R.drawable.mission_dark_bg);

                holder.locationNameAddressText.setVisibility(View.VISIBLE);

                holder.statusText.setBackgroundColor(activity.getResources().getColor(R.color.grey_dark));
                holder.statusText.setTextColor(activity.getResources().getColor(R.color.white));
                holder.statusText.setText(activity.getString(R.string.mission_rejected));

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
}
