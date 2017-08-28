package com.ros.smartrocket.ui.adapter;

import android.app.Activity;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.WavesBL;
import com.ros.smartrocket.db.entity.Wave;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.ui.views.OptionsRow;

import java.util.ArrayList;
import java.util.List;

import static android.text.Html.fromHtml;
import static java.lang.String.format;

public class WaveAdapter extends BaseAdapter {
    private Activity activity;
    private List<Wave> items = new ArrayList<Wave>();
    private LayoutInflater inflater;

    public static class ViewHolder {
        private LinearLayout listItem;
        private TextView name;
        private ImageView image;
        private TextView statusText;
        private OptionsRow optionsRow;
    }

    public WaveAdapter(Activity activity) {
        this.activity = activity;
        inflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Wave getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setData(final List<Wave> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_wave, null);
            vh = new ViewHolder();

            vh.listItem = (LinearLayout) convertView.findViewById(R.id.listItem);
            vh.name = (TextView) convertView.findViewById(R.id.name);
            vh.image = (ImageView) convertView.findViewById(R.id.image);
            vh.statusText = (TextView) convertView.findViewById(R.id.statusText);
            vh.optionsRow = (OptionsRow) convertView.findViewById(R.id.waveItemOptionsRow);

            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        Wave wave = items.get(position);

        vh.name.setText(wave.getName());
        UIUtils.showWaveTypeIcon(activity, vh.image, wave.getIcon());
        vh.optionsRow.setData(wave, false);

        Spanned distance = fromHtml(
                UIUtils.convertMToKm(activity, wave.getNearTaskDistance(), R.string.m_to_km_with_text_mask, true));
        vh.statusText.setText(fromHtml(format(activity.getString(R.string.distance_to_nearest_location), distance)));

        if (WavesBL.isPreClaimWave(wave)) {
            vh.listItem.setBackgroundResource(R.drawable.mission_violet_bg);
            vh.statusText.setBackgroundColor(activity.getResources().getColor(R.color.grey_light));
            vh.statusText.setTextColor(activity.getResources().getColor(R.color.grey));
        } else {
            vh.listItem.setBackgroundResource(R.drawable.mission_green_bg);
            vh.statusText.setBackgroundColor(activity.getResources().getColor(R.color.grey_light));
            vh.statusText.setTextColor(activity.getResources().getColor(R.color.grey));
        }

        return convertView;
    }
}
