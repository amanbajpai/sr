package com.ros.smartrocket.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Answer;

public class AnswerRadioBattonAdapter extends BaseAdapter implements ListAdapter {
    //private static final String TAG = "AnswerCheckBoxAdapter";
    private Answer answers[];
    private LayoutInflater inflater;
    private Activity activity;

    public static class ViewHolder {
        public TextView name;
        public RadioButton radioButton;
    }

    public AnswerRadioBattonAdapter(Activity activity) {
        this.activity = activity;

        this.inflater = LayoutInflater.from(activity);
    }

    public int getCount() {
        if (answers != null) {
            return answers.length;
        } else {
            return 0;
        }
    }

    public Answer getItem(int position) {
        return answers[position];
    }

    public long getItemId(int position) {
        return position;
    }

    public void setData(final Answer[] answers) {
        AnswerRadioBattonAdapter.this.answers = answers;
        notifyDataSetChanged();
    }

    public Answer[] getData() {
        return answers;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.answer_radio_button_row, parent, false);
            holder = new ViewHolder();

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.radioButton = (RadioButton) convertView.findViewById(R.id.radioButton);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Answer category = answers[position];
        holder.name.setText(category.getAnswer());
        holder.radioButton.setChecked(category.isChecked());

        return convertView;
    }
}
