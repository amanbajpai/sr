package com.ros.smartrocket.adapter;

import android.app.Activity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Answer;

public class AnswerCheckBoxAdapter extends BaseAdapter implements ListAdapter {
    //private static final String TAG = "AnswerCheckBoxAdapter";
    private Answer[] answers;
    private LayoutInflater inflater;
    //private Activity activity;

    public static class ViewHolder {
        private TextView name;
        private CheckBox checkBox;
        private EditText otherAnswerEditText;

        public CheckBox getCheckBox() {
            return checkBox;
        }

        public TextView getName() {
            return name;
        }
    }

    public AnswerCheckBoxAdapter(Activity activity) {
        //this.activity = activity;

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
        this.answers = answers;
        notifyDataSetChanged();
    }

    public Answer[] getData() {
        return answers;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.answer_check_box_row, parent, false);
            holder = new ViewHolder();

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            holder.otherAnswerEditText = (EditText) convertView.findViewById(R.id.otherAnswerEditText);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Answer answer = answers[position];
        holder.checkBox.setChecked(answer.isChecked());

        if (Integer.valueOf(answer.getValue()) >= 1000) {
            final CheckBox checkBox = holder.checkBox;
            holder.otherAnswerEditText.setText(answer.getAnswer());
            holder.name.setVisibility(View.GONE);
            holder.otherAnswerEditText.setVisibility(View.VISIBLE);
            holder.otherAnswerEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    answer.setAnswer(s.toString());
                    answer.setChecked(!TextUtils.isEmpty(s.toString()));
                    checkBox.setChecked(!TextUtils.isEmpty(s.toString()));
                }
            });
        } else {
            holder.name.setText(answer.getAnswer());
            holder.name.setVisibility(View.VISIBLE);
            holder.otherAnswerEditText.setVisibility(View.GONE);
        }

        return convertView;
    }
}
