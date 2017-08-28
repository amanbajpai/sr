package com.ros.smartrocket.bl.question;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.images.ImageLoader;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.image.SelectImageManager;

import java.io.File;

import butterknife.BindView;

public final class QuestionInstructionBL extends QuestionBaseBL {
    @BindView(R.id.photo)
    ImageView photoImageView;
    @BindView(R.id.video)
    VideoView videoView;

    @Override
    public void configureView() {
        if (!TextUtils.isEmpty(question.getPhotoUrl())) {
            photoImageView.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(question.getInstructionFileUri())) {
                File file = new File(question.getInstructionFileUri());
                setImageInstructionFile(file);
            } else {
                activity.showLoading(true);

                ImageLoader.getInstance().getFileByUrlAsync(question.getPhotoUrl(),
                        file -> setImageInstructionFile(file)
                );
            }
        } else if (!TextUtils.isEmpty(question.getVideoUrl())) {
            if (!TextUtils.isEmpty(question.getInstructionFileUri())) {
                File file = new File(question.getInstructionFileUri());
                setVideoInstructionFile(file);
            } else {
                activity.hideLoading();

                ImageLoader.getInstance().getFileByUrlAsync(question.getVideoUrl(),
                        file -> setVideoInstructionFile(file)
                );
            }
        }

        refreshNextButton();
    }

    @Override
    public void loadAnswers() {
        // Do nothing
    }

    public void setImageInstructionFile(final File file) {
        Bitmap bitmap = SelectImageManager.prepareBitmap(file, SelectImageManager.SIZE_IN_PX_2_MP, 0, false);
        photoImageView.setImageBitmap(bitmap);
        photoImageView.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(file.getPath())) {
                activity.startActivity(IntentUtils.getFullScreenImageIntent(activity, file.getPath(), false));
            }
        });

        if (activity != null) {
            activity.hideLoading();
        }
    }

    public void setVideoInstructionFile(final File file) {
        videoView.setOnTouchListener((v, event) -> {
            activity.startActivity(IntentUtils.getFullScreenVideoIntent(activity, file.getPath()));
            return false;
        });

        playVideo(file.getPath());
    }

    public void playVideo(String videoPath) {
        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoPath(videoPath);
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            videoView.start();
            videoView.setBackgroundColor(Color.TRANSPARENT);
            if (activity != null) {
                activity.hideLoading();
            }
        });
    }
}