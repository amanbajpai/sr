package com.ros.smartrocket.adapter;

import android.app.Activity;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Wave;
import com.ros.smartrocket.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WaveAdapter extends BaseAdapter {
    private Activity activity;
    private List<Wave> items = new ArrayList<Wave>();
    private LayoutInflater inflater;

    public static class ViewHolder {
        private TextView name;
        private ImageView image;
        private TextView locations;
        private TextView price;
        private TextView exp;
        private TextView distance;
        private TextView textQuestionsCount;
        private TextView photoQuestionsCount;
    }

    public WaveAdapter(Activity activity) {
        this.activity = activity;
        inflater = LayoutInflater.from(activity);
    }

    public int getCount() {
        return items.size();
    }

    public Wave getItem(int position) {
        return items.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public void setData(final List<Wave> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_wave, null);
            holder = new ViewHolder();

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.locations = (TextView) convertView.findViewById(R.id.locations);
            holder.price = (TextView) convertView.findViewById(R.id.price);
            holder.exp = (TextView) convertView.findViewById(R.id.exp);
            holder.distance = (TextView) convertView.findViewById(R.id.distance);
            holder.textQuestionsCount = (TextView) convertView.findViewById(R.id.textQuestionsCount);
            holder.photoQuestionsCount = (TextView) convertView.findViewById(R.id.photoQuestionsCount);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Wave wave = items.get(position);

        holder.name.setText(wave.getName());
        holder.image.setImageResource(UIUtils.getWaveTypeListIcon(1));

        holder.locations.setText(String.valueOf(wave.getTaskCount()));
        holder.price.setText(UIUtils.getBalanceOrPrice(activity, wave.getNearTaskPrice(),
                wave.getNearTaskCurrencySign()));
        holder.exp.setText(String.format(Locale.US, "%.0f", wave.getExperienceOffer()));

        Spanned distance = Html.fromHtml(UIUtils.convertMToKm(activity, wave.getNearTaskDistance(),
                R.string.m_to_km_with_text_mask, true));

        holder.distance.setText(Html.fromHtml(String.format(activity.getString(R.string.distance_to_nearest_location),
                distance)));

        holder.textQuestionsCount.setText(String.valueOf(wave.getNoPhotoQuestionsCount()));
        holder.photoQuestionsCount.setText(String.valueOf(wave.getPhotoQuestionsCount()));


        return convertView;
    }
}
