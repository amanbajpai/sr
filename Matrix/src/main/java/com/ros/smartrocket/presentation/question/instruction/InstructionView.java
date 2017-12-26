package com.ros.smartrocket.presentation.question.instruction;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.question.Answer;
import com.ros.smartrocket.db.entity.question.Question;
import com.ros.smartrocket.presentation.question.base.BaseQuestionView;
import com.ros.smartrocket.utils.IntentUtils;

import java.io.File;
import java.util.List;

import butterknife.BindView;

public class InstructionView extends BaseQuestionView<InstructionMvpPresenter<InstructionMvpView>> implements InstructionMvpView {
    @BindView(R.id.photo)
    ImageView photoView;
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
        return R.layout.view_question_instruction;
    }

    @Override
    public void configureView(Question question) {
        presenter.showInstructions();
        presenter.refreshNextButton(true);
    }

    @Override
    public void fillViewWithAnswers(List<Answer> answers) {
    }

    @Override
    public void setImageInstruction(Bitmap bitmap, String filePath) {
        photoView.setVisibility(VISIBLE);
        photoView.setImageBitmap(bitmap);
        setImageClickListeners(filePath);
    }

    private void setImageClickListeners(String path) {
        photoView.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(path))
                getContext().startActivity(IntentUtils.getFullScreenImageIntent(getContext(), path));
        });
    }

    @Override
    public void setVideoInstructionFile(final File file) {
        video.setOnTouchListener((v, event) -> {
            getContext().startActivity(IntentUtils.getFullScreenVideoIntent(getContext(), file.getPath()));
            return false;
        });
        playVideo(file.getPath());
    }

    private void playVideo(String videoPath) {
        video.setVisibility(View.VISIBLE);
        video.setVideoPath(videoPath);
        video.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            video.start();
            video.setBackgroundColor(Color.TRANSPARENT);
            hideLoading();
        });
    }
}
