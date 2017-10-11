package com.ros.smartrocket.presentation.question.choose.multiple;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.presentation.question.choose.AnswerChoiceBaseAdapter;
import com.ros.smartrocket.presentation.question.choose.BaseChoiceView;

public class MultipleChoiceView extends BaseChoiceView {
    public MultipleChoiceView(Context context) {
        super(context);
    }

    public MultipleChoiceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MultipleChoiceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected AnswerChoiceBaseAdapter getAdapter() {
        return new MultipleChoiceAdapter(getContext(), presenter);
    }

    @Override
    protected int getTitleResId() {
        return R.string.choose_one_or_more_answers;
    }

    @Override
    protected void handleClick(View item, int position) {
        Answer answer = adapter.getItem(position);
        answer.toggleChecked();
        MultipleChoiceAdapter.ViewHolder viewHolder = (MultipleChoiceAdapter.ViewHolder) item.getTag();
        viewHolder.getCheckBox().setChecked(answer.getChecked());
        presenter.refreshNextButton(adapter.getData());
    }
}
