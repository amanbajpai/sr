package com.ros.smartrocket.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.entity.ProgressUpdate;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.utils.LocaleUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.views.OptionsRow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MyTaskAdapter extends BaseAdapter {
    private Activity activity;
    private List<Task> items = new ArrayList<>();
    private LayoutInflater inflater;
    private ProgressUpdate progressUpdate;

    private static class ViewHolder {
        private LinearLayout listItem;

        private TextView name;
        private ImageView image;
        private TextView timeLeft;
        private TextView distance;

        private LinearLayout timeAndDistanceLayout;
        private TextView locationName;
        private TextView missionAvailable;

        private TextView statusText;
        private OptionsRow optionsRow;
    }

    public MyTaskAdapter(Activity activity) {
        this.activity = activity;
        inflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Task getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setData(final List<Task> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        progressUpdate = PreferencesManager.getInstance().getUploadProgress();
        super.notifyDataSetChanged();
    }

    @Override
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
            holder.locationName = (TextView) convertView.findViewById(R.id.locationName);
            holder.missionAvailable = (TextView) convertView.findViewById(R.id.missionAvaileble);

            holder.statusText = (TextView) convertView.findViewById(R.id.statusText);
            holder.optionsRow = (OptionsRow) convertView.findViewById(R.id.taskItemOptionsRow);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Task task = items.get(position);

        holder.optionsRow.setData(task);

        holder.timeAndDistanceLayout.setVisibility(View.GONE);
        holder.locationName.setVisibility(View.GONE);
        holder.missionAvailable.setVisibility(View.GONE);

        holder.name.setText(task.getName());
        UIUtils.showWaveTypeIcon(activity, holder.image, task.getIcon());
        if (!TextUtils.isEmpty(task.getLocationName()) || !TextUtils.isEmpty(task.getAddress())) {
            holder.locationName.setText(task.getLocationName() + " " + task.getAddress());
        }

        long startTimeInMillisecond = task.getLongStartDateTime();
        long expireTimeInMillisecond = task.getLongExpireDateTime();
        long dueInMillisecond = expireTimeInMillisecond - Calendar.getInstance().getTimeInMillis();
        setTimeLeft(holder.timeLeft, UIUtils.getTimeInDayHoursMinutes(activity, dueInMillisecond));

        holder.distance.setText(UIUtils.convertMToKm(activity, task.getDistance(), R.string.m_to_km_with_text_mask, true));

        switch (TasksBL.getTaskStatusType(task.getStatusId())) {
            case NONE:
            case CLAIMED:
            case STARTED:
                if (TasksBL.isPreClaimTask(task)) {
                    holder.listItem.setBackgroundResource(R.drawable.mission_violet_bg);

                    holder.missionAvailable.setText(activity.getString(R.string.mission_available, UIUtils
                            .longToString(startTimeInMillisecond, 3)));
                    holder.missionAvailable.setVisibility(View.VISIBLE);

                    holder.statusText.setBackgroundColor(activity.getResources().getColor(R.color.grey_light));
                    holder.statusText.setTextColor(activity.getResources().getColor(R.color.grey));
                    holder.statusText.setText(activity.getString(R.string.mission_expires_at,
                            UIUtils.longToString(expireTimeInMillisecond, 3)));
                } else {
                    holder.listItem.setBackgroundResource(R.drawable.mission_green_bg);

                    holder.timeAndDistanceLayout.setVisibility(View.VISIBLE);

                    holder.statusText.setBackgroundColor(activity.getResources().getColor(R.color.grey_light));
                    holder.statusText.setTextColor(activity.getResources().getColor(R.color.grey));
                    holder.statusText.setText(activity.getString(R.string.mission_expires_at,
                            UIUtils.longToString(expireTimeInMillisecond, 3)));
                }
                break;
            case SCHEDULED:
            case PENDING:
                holder.listItem.setBackgroundResource(R.drawable.mission_blue_bg);

                holder.timeAndDistanceLayout.setVisibility(View.VISIBLE);

                holder.statusText.setBackgroundColor(activity.getResources().getColor(R.color.blue_light));
                holder.statusText.setTextColor(activity.getResources().getColor(R.color.white));
                holder.statusText.setText(activity.getString(R.string.send_latter_mission,
                        UIUtils.longToString(expireTimeInMillisecond, 3)));
                break;
            case COMPLETED:
                holder.listItem.setBackgroundResource(R.drawable.mission_grey_bg);

                holder.locationName.setVisibility(View.VISIBLE);

                holder.statusText.setBackgroundColor(activity.getResources().getColor(R.color.grey_light));
                holder.statusText.setTextColor(activity.getResources().getColor(R.color.grey));
                if (progressUpdate!=null && task.getId().equals(progressUpdate.getTaskId())){
                    holder.statusText.setText(activity.getString(R.string.mission_transmitting, getProgress()));
                } else {
                    holder.statusText.setText(activity.getString(R.string.mission_transmitting, ""));
                }
                break;
            case VALIDATION:
                holder.listItem.setBackgroundResource(R.drawable.mission_grey_bg);

                holder.locationName.setVisibility(View.VISIBLE);

                holder.statusText.setBackgroundColor(activity.getResources().getColor(R.color.grey_light));
                holder.statusText.setTextColor(activity.getResources().getColor(R.color.grey));
                holder.statusText.setText(activity.getString(R.string.mission_in_validation));
                break;
            case RE_DO_TASK:
                holder.statusText.setBackgroundColor(activity.getResources().getColor(R.color.red));
                holder.statusText.setTextColor(activity.getResources().getColor(R.color.white));

                if (expireTimeInMillisecond != 0) {
                    holder.timeLeft.setVisibility(View.VISIBLE);
                    holder.statusText.setText(activity.getString(R.string.redo_mission,
                            UIUtils.longToString(expireTimeInMillisecond, 3)));
                } else {
                    holder.timeLeft.setVisibility(View.INVISIBLE);
                    holder.statusText.setText(activity.getString(R.string.redo_mission, ""));
                }

                holder.listItem.setBackgroundResource(R.drawable.mission_red_bg);
                holder.timeAndDistanceLayout.setVisibility(View.VISIBLE);
                break;
            case VALIDATED:
            case IN_PAYMENT_PROCESS:
            case PAID:
                holder.listItem.setBackgroundResource(R.drawable.mission_gold_bg);

                holder.locationName.setVisibility(View.VISIBLE);

                holder.statusText.setBackgroundColor(activity.getResources().getColor(R.color.yellow));
                holder.statusText.setTextColor(activity.getResources().getColor(R.color.white));
                holder.statusText.setText(activity.getString(R.string.mission_validated));
                break;
            case REJECTED:
                holder.listItem.setBackgroundResource(R.drawable.mission_dark_bg);

                holder.locationName.setVisibility(View.VISIBLE);

                holder.statusText.setBackgroundColor(activity.getResources().getColor(R.color.grey_dark));
                holder.statusText.setTextColor(activity.getResources().getColor(R.color.white));
                holder.statusText.setText(activity.getString(R.string.mission_rejected));
                break;
            default:
                break;
        }

        return convertView;
    }

    private String getProgress(){
        StringBuilder sb = new StringBuilder(" ");
        sb.append(progressUpdate.getUploadedFilesCount());
        sb.append("/");
        sb.append(progressUpdate.getTotalFilesCount());
        return sb.toString();
    }

    private void setTimeLeft(TextView timeLeftTextView, String timeLeft) {
        timeLeftTextView.setVisibility(View.VISIBLE);
        if (LocaleUtils.isChinaLanguage()) {
            timeLeftTextView.setText(activity.getString(R.string.time_left) + " " + timeLeft);
        } else {
            timeLeftTextView.setText(timeLeft + " " + activity.getString(R.string.time_left));
        }
    }
}
