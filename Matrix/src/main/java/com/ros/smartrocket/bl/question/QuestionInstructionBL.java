package com.ros.smartrocket.bl.question;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;
import butterknife.Bind;
import com.ros.smartrocket.R;
import com.ros.smartrocket.images.ImageLoader;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.image.SelectImageManager;

import java.io.File;

public final class QuestionInstructionBL extends QuestionBaseBL {
    @Bind(R.id.photo)
    ImageView photoImageView;
    @Bind(R.id.video)
    VideoView videoView;

    @Override
    public void configureView() {
        if (!TextUtils.isEmpty(question.getPhotoUrl())) {
            photoImageView.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(question.getInstructionFileUri())) {
                File file = new File(question.getInstructionFileUri());
                setImageInstructionFile(file);
            } else {
                ((ActionBarActivity) activity).setSupportProgressBarIndeterminateVisibility(true);

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
                ((ActionBarActivity) activity).setSupportProgressBarIndeterminateVisibility(true);

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
    }

    @Override
    public void loadAnswers() {
        // Do nothing
    }

    public void setImageInstructionFile(final File file) {
        Bitmap bitmap = SelectImageManager.prepareBitmap(file, SelectImageManager.SIZE_IN_PX_2_MP, 0, false);
        photoImageView.setImageBitmap(bitmap);
        photoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(file.getPath())) {
                    activity.startActivity(IntentUtils.getFullScreenImageIntent(activity, file.getPath(), false));
                }
            }
        });

        if (activity != null) {
            ((ActionBarActivity) activity).setSupportProgressBarIndeterminateVisibility(false);
        }
    }

    public void setVideoInstructionFile(final File file) {
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                activity.startActivity(IntentUtils.getFullScreenVideoIntent(activity, file.getPath()));
                return false;
            }
        });

        playVideo(file.getPath());
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
                if (activity != null) {
                    ((ActionBarActivity) activity).setSupportProgressBarIndeterminateVisibility(false);
                }
            }
        });
    }
}