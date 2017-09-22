package com.ros.smartrocket.presentation.question.instruction;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.presentation.details.claim.MediaDownloader;
import com.ros.smartrocket.presentation.question.base.QuestionBaseBL;
import com.ros.smartrocket.utils.FileProcessingManager;
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
                MediaDownloader md = new MediaDownloader(FileProcessingManager.FileType.IMAGE, new MediaDownloader.OnFileLoadCompleteListener() {
                    @Override
                    public void onFileLoadComplete(File result) {
                        setImageInstructionFile(result);
                    }

                    @Override
                    public void onFileLoadError() {
                        hideLoading();
                    }
                });
                md.getMediaFileAsync(question.getPhotoUrl());
            }
        } else if (!TextUtils.isEmpty(question.getVideoUrl())) {
            if (!TextUtils.isEmpty(question.getInstructionFileUri())) {
                File file = new File(question.getInstructionFileUri());
                setVideoInstructionFile(file);
            } else {
                activity.hideLoading();
                MediaDownloader md = new MediaDownloader(FileProcessingManager.FileType.VIDEO, new MediaDownloader.OnFileLoadCompleteListener() {
                    @Override
                    public void onFileLoadComplete(File result) {
                        setVideoInstructionFile(result);
                    }

                    @Override
                    public void onFileLoadError() {
                        hideLoading();
                    }
                });
                md.getMediaFileAsync(question.getVideoUrl());
            }
        }
        refreshNextButton();
    }

    @Override
    public void loadAnswers() {
        // Do nothing
    }

    private void setImageInstructionFile(final File file) {
        Bitmap bitmap = SelectImageManager.prepareBitmap(file, SelectImageManager.SIZE_IN_PX_2_MP, 0);
        photoImageView.setImageBitmap(bitmap);
        setImageClickListeners(file.getPath());
        hideLoading();
    }

    private void hideLoading() {
        if (activity != null)
            activity.hideLoading();
    }

    private void setImageClickListeners(String path) {
        photoImageView.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(path)) {
                activity.startActivity(IntentUtils.getFullScreenImageIntent(activity, path));
            }
        });
    }

    private void setVideoInstructionFile(final File file) {
        videoView.setOnTouchListener((v, event) -> {
            activity.startActivity(IntentUtils.getFullScreenVideoIntent(activity, file.getPath()));
            return false;
        });
        playVideo(file.getPath());
    }

    private void playVideo(String videoPath) {
        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoPath(videoPath);
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            videoView.start();
            videoView.setBackgroundColor(Color.TRANSPARENT);
            hideLoading();
        });
    }
}