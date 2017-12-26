package com.ros.smartrocket.presentation.question.choose;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import com.ros.smartrocket.db.entity.question.Answer;

import java.util.List;

public abstract class AnswerChooseBaseAdapter extends BaseAdapter implements ListAdapter {
    protected List<Answer> answers;
    protected LayoutInflater inflater;
    protected ChooseMvpPresenter<ChooseMvpView> presenter;

    public AnswerChooseBaseAdapter(Context context, ChooseMvpPresenter<ChooseMvpView> presenter) {
        this.inflater = LayoutInflater.from(context);
        this.presenter = presenter;
    }

    @Override
    public int getCount() {
        if (answers != null)
            return answers.size();
        else
            return 0;
    }

    @Override
    public Answer getItem(int position) {
        return answers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setData(final List<Answer> answers) {
        this.answers = answers;
        notifyDataSetChanged();
    }

    public List<Answer> getData() {
        return answers;
    }
}
