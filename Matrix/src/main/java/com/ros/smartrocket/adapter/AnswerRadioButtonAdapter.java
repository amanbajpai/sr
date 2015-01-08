package com.ros.smartrocket.adapter;

import android.app.Activity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.interfaces.OnAnswerSelectedListener;

public class AnswerRadioButtonAdapter extends BaseAdapter implements ListAdapter {
    private Answer[] answers;
    private LayoutInflater inflater;
    private OnAnswerSelectedListener answerSelectedListener;

    public static class ViewHolder {
        private TextView name;
        private RadioButton radioButton;
        private EditText otherAnswerEditText;

        public TextView getName() {
            return name;
        }
    }

    public AnswerRadioButtonAdapter(Activity activity, OnAnswerSelectedListener answerSelectedListener) {
        this.inflater = LayoutInflater.from(activity);
        this.answerSelectedListener = answerSelectedListener;
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
            convertView = inflater.inflate(R.layout.answer_radio_button_row, parent, false);
            holder = new ViewHolder();

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.radioButton = (RadioButton) convertView.findViewById(R.id.radioButton);
            holder.otherAnswerEditText = (EditText) convertView.findViewById(R.id.otherAnswerEditText);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Answer answer = answers[position];
        holder.radioButton.setChecked(answer.getChecked());

        if (Integer.valueOf(answer.getValue()) >= 1000) {
            holder.otherAnswerEditText.setText(answer.getAnswer());
            holder.name.setVisibility(View.GONE);
            holder.otherAnswerEditText.setVisibility(View.VISIBLE);

            if (answer.getChecked()) {
                holder.otherAnswerEditText.requestFocus();
            }

            holder.otherAnswerEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        for (Answer answerToIt : answers) {
                            answerToIt.setChecked(answerToIt == answer);
                        }

                        notifyDataSetChanged();
                    }

                }
            });
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

                    if (answerSelectedListener != null) {
                        boolean selected = false;
                        for (Answer answer : getData()) {
                            if (answer.getChecked() && !TextUtils.isEmpty(answer.getAnswer())) {
                                selected = true;
                                break;
                            }
                        }
                        answerSelectedListener.onAnswerSelected(selected);
                    }
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
