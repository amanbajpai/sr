package com.ros.smartrocket.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.interfaces.OnAnswerSelectedListener;

public abstract class AnswerBaseAdapter extends BaseAdapter implements ListAdapter {
    protected Answer[] answers;
    protected LayoutInflater inflater;
    protected OnAnswerSelectedListener answerSelectedListener;

    public AnswerBaseAdapter(Context context, OnAnswerSelectedListener answerSelectedListener) {
        this.inflater = LayoutInflater.from(context);
        this.answerSelectedListener = answerSelectedListener;
    }

    @Override
    public int getCount() {
        if (answers != null) {
            return answers.length;
        } else {
            return 0;
        }
    }

    @Override
    public Answer getItem(int position) {
        return answers[position];
    }

    @Override
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
}
