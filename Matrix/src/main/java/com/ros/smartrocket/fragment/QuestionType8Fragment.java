package com.ros.smartrocket.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.images.ImageLoader;
import com.ros.smartrocket.interfaces.OnAnswerPageLoadingFinishedListener;
import com.ros.smartrocket.interfaces.OnAnswerSelectedListener;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.SelectImageManager;

import java.io.File;

/**
 * Instraction question type
 */
public class QuestionType8Fragment extends BaseQuestionFragment {
    private Question question;
    private ImageView photoImageView;
    private VideoView videoView;
    private OnAnswerSelectedListener answerSelectedListener;
    private OnAnswerPageLoadingFinishedListener answerPageLoadingFinishedListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        ViewGroup view = (ViewGroup) localInflater.inflate(R.layout.fragment_question_type_8, null);

        if (getArguments() != null) {
            question = (Question) getArguments().getSerializable(Keys.QUESTION);
        }

        TextView questionText = (TextView) view.findViewById(R.id.questionText);
        if (!TextUtils.isEmpty(question.getPresetValidationText())) {
            TextView presetValidationComment = (TextView) view.findViewById(R.id.presetValidationComment);
            presetValidationComment.setText(question.getPresetValidationText());
            presetValidationComment.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(question.getValidationComment())) {
            TextView validationComment = (TextView) view.findViewById(R.id.validationComment);
            validationComment.setText(question.getValidationComment());
            validationComment.setVisibility(View.VISIBLE);
        }

        photoImageView = (ImageView) view.findViewById(R.id.photo);

        videoView = (VideoView) view.findViewById(R.id.video);

        questionText.setText(question.getQuestion());

        if (!TextUtils.isEmpty(question.getPhotoUrl())) {
            if (!TextUtils.isEmpty(question.getInstructionFileUri())) {
                File file = new File(question.getInstructionFileUri());
                setImageInstructionFile(file);
            } else {
                ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(true);

                photoImageView.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().getFileByUrlAsync(question.getPhotoUrl(),
                        new ImageLoader.OnFileLoadCompleteListener() {
                            @Override
                            public void onFileLoadComplete(final File file) {
                                setImageInstructionFile(file);
                            }
                        }
                );
            }
        } else if (!TextUtils.isEmpty(question.getVideoUrl())) {
            if (!TextUtils.isEmpty(question.getInstructionFileUri())) {
                File file = new File(question.getInstructionFileUri());
                setVideoInstructionFile(file);
            } else {
                ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(true);

                ImageLoader.getInstance().getFileByUrlAsync(question.getVideoUrl(),
                        new ImageLoader.OnFileLoadCompleteListener() {
                            @Override
                            public void onFileLoadComplete(final File file) {
                                setVideoInstructionFile(file);
                            }
                        }
                );
            }
        }

        refreshNextButton();
        return view;
    }

    public void setImageInstructionFile(final File file) {
        Bitmap bitmap = SelectImageManager.prepareBitmap(file, SelectImageManager.SIZE_IN_PX_2_MP, 0, false);
        photoImageView.setImageBitmap(bitmap);
        photoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(IntentUtils.getFullScreenImageIntent(getActivity(), file.getPath(), false));
            }
        });

        if (getActivity() != null) {
            ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(false);
        }
    }

    public void setVideoInstructionFile(final File file) {
        //videoView.setMediaController(new CustomMediaController(getActivity()));
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                startActivity(IntentUtils.getFullScreenVideoIntent(getActivity(), file.getPath()));
                return false;
            }
        });

        playVideo(file.getPath());
    }

    public void refreshNextButton() {
        if (answerSelectedListener != null) {
            answerSelectedListener.onAnswerSelected(true);
        }

        if (answerPageLoadingFinishedListener != null) {
            answerPageLoadingFinishedListener.onAnswerPageLoadingFinished();
        }
    }

    public void playVideo(String videoPath) {
        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoPath(videoPath);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                videoView.start();
                videoView.setBackgroundColor(Color.TRANSPARENT);
                if (getActivity() != null) {
                    ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(false);
                }
            }
        });
    }

    @Override
    public boolean saveQuestion() {
        return true;
    }

    @Override
    public void clearAnswer() {
    }

    @Override
    public Question getQuestion() {
        return question;
    }


    @Override
    public void setAnswerSelectedListener(OnAnswerSelectedListener answerSelectedListener) {
        this.answerSelectedListener = answerSelectedListener;
    }

    @Override
    public void setAnswerPageLoadingFinishedListener(OnAnswerPageLoadingFinishedListener
                                                             answerPageLoadingFinishedListener) {
        this.answerPageLoadingFinishedListener = answerPageLoadingFinishedListener;
    }
}
