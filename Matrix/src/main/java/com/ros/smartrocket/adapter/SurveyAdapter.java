package com.ros.smartrocket.adapter;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Survey;
import com.ros.smartrocket.utils.UIUtils;

import java.util.ArrayList;
import java.util.Locale;

public class SurveyAdapter extends BaseAdapter {
    // private static final String TAG = "SurveyAdapter";
    private Activity activity;
    private ArrayList<Survey> items = new ArrayList<Survey>();
    private LayoutInflater inflater;

    public static class ViewHolder {
        private TextView name;
        private TextView description;
        private ImageView image;
        private TextView locations;
        private TextView price;
        private TextView exp;
        private TextView distance;
    }

    public SurveyAdapter(Activity activity) {
        this.activity = activity;
        inflater = LayoutInflater.from(activity);
    }

    public int getCount() {
        return items.size();
    }

    public Survey getItem(int position) {
        return items.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public void setData(final ArrayList<Survey> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void addData(final ArrayList<Survey> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_survey, null);
            holder = new ViewHolder();

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.description = (TextView) convertView.findViewById(R.id.description);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.locations = (TextView) convertView.findViewById(R.id.locations);
            holder.price = (TextView) convertView.findViewById(R.id.price);
            holder.exp = (TextView) convertView.findViewById(R.id.exp);
            holder.distance = (TextView) convertView.findViewById(R.id.distance);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Survey survey = items.get(position);

        holder.name.setText(survey.getName());
        holder.description.setText(survey.getDescription());
        holder.image.setImageResource(R.drawable.ic_launcher);

        holder.locations.setText(Html.fromHtml(String.format(activity.getString(R.string.locations),
                survey.getTaskCount())));

        holder.price.setText(Html.fromHtml(String.format(activity.getString(R.string.survey_price),
                String.format(Locale.US, "%.1f", survey.getNearTaskPrice()))));

        //TODO Get EXP from survey
        holder.exp.setText(Html.fromHtml(String.format(activity.getString(R.string.survey_exp),
                String.format(Locale.US, "%,d", 130))));

        holder.distance.setText(Html.fromHtml(UIUtils.convertMToKm(activity, survey.getNearTaskDistance(), R.string.survey_distance)));

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