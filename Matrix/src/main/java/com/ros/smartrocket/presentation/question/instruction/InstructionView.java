package com.ros.smartrocket.presentation.question.instruction;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.VideoView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.presentation.question.base.BaseQuestionView;

import butterknife.BindView;

public class InstructionView extends BaseQuestionView implements InstructionMvpView {
    @BindView(R.id.photo)
    ImageView photo;
    @BindView(R.id.video)
    VideoView video;

    public InstructionView(Context context) {
        super(context);
    }

    public InstructionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public InstructionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_question_instruction;
    }

    @Override
    public void configureView(Question question) {

    }

    @Override
    public void fillViewWithAnswers(Answer[] answers) {

    }
}
