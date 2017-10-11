package com.ros.smartrocket.presentation.question.choose;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import com.ros.smartrocket.db.entity.Answer;

import java.util.List;

public abstract class AnswerChoiceBaseAdapter extends BaseAdapter implements ListAdapter {
    protected List<Answer> answers;
    protected LayoutInflater inflater;
    protected ChoiceMvpPresenter<ChoiceMvpView> presenter;

    public AnswerChoiceBaseAdapter(Context context, ChoiceMvpPresenter<ChoiceMvpView> presenter) {
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
