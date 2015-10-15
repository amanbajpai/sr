package com.ros.smartrocket.adapter;

import android.app.Activity;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ros.smartrocket.BuildConfig;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.WavesBL;
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
        private LinearLayout listItem;
        private TextView name;
        private ImageView image;
        private TextView locations;
        private TextView price;
        private TextView exp;
        private TextView statusText;
        private TextView textQuestionsCount;
        private TextView photoQuestionsCount;
        private LinearLayout optionLayout;
        private View optionDivider;
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

            holder.listItem = (LinearLayout) convertView.findViewById(R.id.listItem);

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.locations = (TextView) convertView.findViewById(R.id.locations);
            holder.price = (TextView) convertView.findViewById(R.id.price);
            holder.exp = (TextView) convertView.findViewById(R.id.exp);
            holder.statusText = (TextView) convertView.findViewById(R.id.statusText);
            holder.textQuestionsCount = (TextView) convertView.findViewById(R.id.textQuestionsCount);
            holder.photoQuestionsCount = (TextView) convertView.findViewById(R.id.photoQuestionsCount);

            holder.optionLayout = (LinearLayout) convertView.findViewById(R.id.optionLayout);
            holder.optionDivider = convertView.findViewById(R.id.optionDivider);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Wave wave = items.get(position);

        holder.name.setText(wave.getName());
        UIUtils.showWaveTypeIcon(activity, holder.image, wave.getIcon());
        holder.locations.setText(String.valueOf(wave.getTaskCount()));
        holder.price.setText(UIUtils.getBalanceOrPrice(activity, wave.getNearTaskPrice(),
                wave.getNearTaskCurrencySign(), null, null));

        holder.exp.setText(String.format(Locale.US, "%.0f", wave.getExperienceOffer()));

        Spanned distance = Html.fromHtml(UIUtils.convertMToKm(activity, wave.getNearTaskDistance(),
                R.string.m_to_km_with_text_mask, true));

        holder.statusText.setText(Html.fromHtml(String.format(activity.getString(R.string.distance_to_nearest_location),
                distance)));

        holder.textQuestionsCount.setText(String.valueOf(wave.getNoPhotoQuestionsCount()));
        holder.photoQuestionsCount.setText(String.valueOf(wave.getPhotoQuestionsCount()));

//        if (BuildConfig.DEBUG && position % 2 == 0) {
//            wave.setContainsDifferentRate(true);
//        }

        if (WavesBL.isPreClaimWave(wave)) {
            holder.listItem.setBackgroundResource(R.drawable.mission_violet_bg);

            holder.statusText.setBackgroundColor(activity.getResources().getColor(R.color.grey_light));
            holder.statusText.setTextColor(activity.getResources().getColor(R.color.grey));

            holder.optionLayout.setBackgroundColor(activity.getResources().getColor(R.color.violet));
            holder.optionDivider.setBackgroundColor(activity.getResources().getColor(R.color.violet_light));

            if (wave.isContainsDifferentRate()) {
                holder.price.append("+");
            }

            holder.price.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_violet, 0, 0, 0);
            holder.exp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket_violet, 0, 0, 0);
            holder.locations.setCompoundDrawablesWithIntrinsicBounds(R.drawable.location_violet, 0, 0, 0);
            holder.textQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.quote_violet, 0, 0, 0);
            holder.photoQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera_violet, 0, 0, 0);
        } else {
            holder.listItem.setBackgroundResource(R.drawable.mission_green_bg);

            holder.statusText.setBackgroundColor(activity.getResources().getColor(R.color.grey_light));
            holder.statusText.setTextColor(activity.getResources().getColor(R.color.grey));

            holder.optionLayout.setBackgroundColor(activity.getResources().getColor(R.color.green));
            holder.optionDivider.setBackgroundColor(activity.getResources().getColor(R.color.green_light));

            if (wave.isContainsDifferentRate()) {
                holder.price.append("+");
            }

            holder.price.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_green, 0, 0, 0);
            holder.exp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket_green, 0, 0, 0);
            holder.locations.setCompoundDrawablesWithIntrinsicBounds(R.drawable.location_green, 0, 0, 0);
            holder.textQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.quote_green, 0, 0, 0);
            holder.photoQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera_green, 0, 0, 0);
        }


        return convertView;
    }
}
