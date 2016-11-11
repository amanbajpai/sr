package com.ros.smartrocket.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.interfaces.OnAnswerSelectedListener;

public class AnswerCheckBoxAdapter extends AnswerBaseAdapter {
    public AnswerCheckBoxAdapter(Context context, OnAnswerSelectedListener answerSelectedListener) {
        super(context, answerSelectedListener);
    }

    @Override
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
        holder.checkBox.setChecked(answer.getChecked());

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

                    if (answerSelectedListener != null) {
                        boolean selected = false;
                        for (Answer answer : getData()) {
                            if (answer.getChecked()) {
                                selected = true;
                                break;
                            }
                        }
                        answerSelectedListener.onAnswerSelected(selected, answer.getQuestionId());
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
}
