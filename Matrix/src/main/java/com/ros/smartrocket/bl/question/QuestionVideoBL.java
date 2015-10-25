package com.ros.smartrocket.bl.question;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.VideoView;
import com.ros.smartrocket.R;
import com.ros.smartrocket.activity.QuestionsActivity;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.fragment.QuestionVideoFragment;
import com.ros.smartrocket.location.MatrixLocationManager;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.SelectVideoManager;
import com.ros.smartrocket.utils.UIUtils;

import java.io.File;

public final class QuestionVideoBL extends QuestionBaseBL implements View.OnClickListener,
        MediaPlayer.OnCompletionListener {
    private static final String TAG = QuestionVideoFragment.class.getSimpleName();
    private SelectVideoManager selectVideoManager = SelectVideoManager.getInstance();
    private String videoPath;
    private boolean isVideoAdded = false;
    private boolean isVideoConfirmed = false;
    private int stopPosition = 0;

    private ImageButton rePhotoButton;
    private ImageButton confirmButton;
    private VideoView videoView;

    @Override
    public void initView(View view, Question question, Bundle savedInstanceState) {
        super.initView(view, question, savedInstanceState);

        videoView = (VideoView) view.findViewById(R.id.video);
        videoView.setOnCompletionListener(this);

        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    onClick(v);
                }

                return false;
            }
        });

        rePhotoButton = (ImageButton) view.findViewById(R.id.rePhotoButton);
        rePhotoButton.setOnClickListener(this);

        confirmButton = (ImageButton) view.findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(this);

        loadAnswers();
    }

    @Override
    public void clearAnswer() {
        if (question != null && question.getAnswers() != null && question.getAnswers().length > 0) {
            Answer[] answers = question.getAnswers();
            for (Answer answer : answers) {
                answer.setChecked(false);
            }

            AnswersBL.updateAnswersToDB(handler, answers);
        }
    }

    @Override
    public void fillViewWithAnswers(Answer[] answers) {
        question.setAnswers(answers);

        Answer answer = answers[0];
        if (answer.getChecked() && answer.getFileUri() != null) {
            isVideoAdded = true;
            isVideoConfirmed = true;
            videoPath = Uri.parse(answer.getFileUri()).getPath();

            playPauseVideo(videoPath);
        } else {
            isVideoAdded = false;
            isVideoConfirmed = false;

            videoView.setVisibility(View.VISIBLE);
            videoView.setBackgroundResource(R.drawable.camera_video_icon);
        }

        refreshRePhotoButton();
        refreshConfirmButton();
        refreshNextButton();
    }

    @Override
    protected void answersUpdate() {
        ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(false);
    }

    @Override
    public void refreshNextButton() {
        if (answerSelectedListener != null) {
            Answer answer = question.getAnswers()[0];
            boolean selected = answer.getFileUri() != null;

            answerSelectedListener.onAnswerSelected(selected);
        }

        if (answerPageLoadingFinishedListener != null) {
            answerPageLoadingFinishedListener.onAnswerPageLoadingFinished();
        }
    }

    public void refreshConfirmButton() {
        if (isVideoAdded) {
            confirmButton.setVisibility(View.VISIBLE);
            if (isVideoConfirmed) {
                confirmButton.setBackgroundResource(R.drawable.btn_square_green);
                confirmButton.setImageResource(R.drawable.check_square_white);
            } else {
                confirmButton.setBackgroundResource(R.drawable.btn_square_active);
                confirmButton.setImageResource(R.drawable.check_square_green);
            }
        } else {
            confirmButton.setVisibility(View.GONE);
        }

    }

    public void refreshRePhotoButton() {
        if (isVideoAdded) {
            rePhotoButton.setVisibility(View.VISIBLE);
        } else {
            rePhotoButton.setVisibility(View.GONE);
        }
    }

    public void playVideo() {
        videoView.seekTo(stopPosition);
        videoView.start();
        videoView.setBackgroundColor(Color.TRANSPARENT);
    }

    public void pauseVideo() {
        stopPosition = videoView.getCurrentPosition();
        videoView.pause();
    }

    public void playPauseVideo(String videoPath) {
        ((QuestionsActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(true);
        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoPath(videoPath);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        if (getActivity() != null) {
                            ((QuestionsActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(false);
                        }
                        videoView.setBackgroundColor(Color.TRANSPARENT);
                        videoView.pause();
                    }
                }, 700);
            }
        });
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent intent) {
        selectVideoManager.onActivityResult(requestCode, resultCode, intent);
        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopPosition = 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video:
                if (isVideoAdded) {
                    if (videoView.isPlaying()) {
                        pauseVideo();
                    } else {
                        playVideo();
                    }
                    break;
                }
            case R.id.rePhotoButton:
//                System.gc();

                if (question.getVideoSource() == 0) {
                    selectVideoManager.startCamera(getActivity());
                } else if (question.getVideoSource() == 1) {
                    selectVideoManager.startGallery(getActivity());
                } else {
                    selectVideoManager.showSelectVideoDialog(getActivity(), true);
                }

                selectVideoManager.setVideoCompleteListener(new SelectVideoManager.OnVideoCompleteListener() {
                    @Override
                    public void onVideoComplete(String videoFilePath) {
                        File sourceImageFile = new File(videoFilePath);

                        L.e(TAG, "Free Memory size: " + UIUtils.getMemorySize(1) / 1000 + " and File size " + sourceImageFile.length() / 1000);

                        if (sourceImageFile.length() > getActivity().getResources().getInteger(R.integer.max_video_file_size_byte)) {
                            DialogUtils.showBigFileToUploadDialog(getActivity());
                        } else {
                            videoPath = videoFilePath;

                            isVideoAdded = !TextUtils.isEmpty(videoPath);
                            isVideoConfirmed = false;
                            answerSelectedListener.onAnswerSelected(false);

                            if (!TextUtils.isEmpty(videoPath)) {
                                playPauseVideo(videoPath);
                            } else {
                                videoView.setBackgroundResource(R.drawable.camera_video_icon);
                            }

                            refreshRePhotoButton();
                            refreshConfirmButton();
                        }
                    }

                    @Override
                    public void onSelectVideoError(int imageFrom) {
                        DialogUtils.showPhotoCanNotBeAddDialog(getActivity());
                    }
                });
                break;
            case R.id.confirmButton:
                if (!isVideoConfirmed) {
                    MatrixLocationManager.getCurrentLocation(false, new MatrixLocationManager
                            .GetCurrentLocationListener() {
                        @Override
                        public void getLocationStart() {
                            ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(true);
                        }

                        @Override
                        public void getLocationInProcess() {
                        }

                        @Override
                        public void getLocationSuccess(Location location) {
                            if (getActivity() == null) {
                                return;
                            }

                            confirmButtonPressAction(location);
                            ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(false);
                        }

                        @Override
                        public void getLocationFail(String errorText) {
                            if (!getActivity().isFinishing()) {
                                UIUtils.showSimpleToast(getActivity(), errorText);
                            }
                        }
                    });
                }
                break;
            default:
                break;
        }
    }

    public void confirmButtonPressAction(Location location) {
        File sourceImageFile = new File(videoPath);

        if (sourceImageFile.exists()) {
            Answer answer = question.getAnswers()[0];
            answer.setChecked(true);
            answer.setFileUri(videoPath);
            answer.setFileSizeB(sourceImageFile.length());
            answer.setFileName(sourceImageFile.getName());
            answer.setValue(sourceImageFile.getName());
            answer.setLatitude(location.getLatitude());
            answer.setLongitude(location.getLongitude());

            AnswersBL.updateAnswersToDB(handler, question.getAnswers());

            isVideoConfirmed = true;

            refreshRePhotoButton();
            refreshConfirmButton();
            refreshNextButton();
        }
    }
}
