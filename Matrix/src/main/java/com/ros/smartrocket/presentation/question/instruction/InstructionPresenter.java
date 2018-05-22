package com.ros.smartrocket.presentation.question.instruction;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.ros.smartrocket.db.entity.question.Question;
import com.ros.smartrocket.presentation.details.claim.MediaDownloader;
import com.ros.smartrocket.presentation.question.base.BaseQuestionPresenter;
import com.ros.smartrocket.utils.FileProcessingManager;
import com.ros.smartrocket.utils.image.SelectImageManager;

import java.io.File;

public class InstructionPresenter<V extends InstructionMvpView> extends BaseQuestionPresenter<V> implements InstructionMvpPresenter<V> {
    File file = null;

    public InstructionPresenter(Question question) {
        super(question);
    }

    @Override
    public boolean saveQuestion() {
        return true;
    }

    @Override
    public void showInstructions() {
        if (!TextUtils.isEmpty(question.getPhotoUrl()))
            showPhotoInstruction();
        else if (!TextUtils.isEmpty(question.getVideoUrl()))
            showVideoInstruction();
    }

    private void showVideoInstruction() {
        if (!TextUtils.isEmpty(question.getInstructionFileUri())) {
            File file = new File(question.getInstructionFileUri());
            getMvpView().setVideoInstructionFile(file);
        } else {
            getMvpView().showLoading(true);
            MediaDownloader md = new MediaDownloader(FileProcessingManager.FileType.VIDEO, new MediaDownloader.OnFileLoadCompleteListener() {
                @Override
                public void onFileLoadComplete(File result) {
                    if (isViewAttached()) getMvpView().setVideoInstructionFile(result);
                }

                @Override
                public void onFileLoadError() {
                    if (isViewAttached()) getMvpView().hideLoading();
                }
            });
            md.getMediaFileAsync(question.getVideoUrl());
        }
    }

    private void showPhotoInstruction() {
//        PhotoLoader.getBitmapFromUrl(question.getPhotoUrl(), new Target() {
//            @Override
//            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                getMvpView().hideLoading();
//                file = SelectImageManager.saveBitmapToFile(App.getInstance(), bitmap, "");
//                getMvpView().setImageInstruction(bitmap, file.getPath().toString());
//            }
//
//            @Override
//            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
//
//            }
//
//            @Override
//            public void onPrepareLoad(Drawable placeHolderDrawable) {
//                getMvpView().showLoading(true);
//            }
//        });

        if (!TextUtils.isEmpty(question.getInstructionFileUri())) {
            File file = new File(question.getInstructionFileUri());
            getMvpView().setImageInstruction(getBitmap(file), file.getPath());
        } else {
            getMvpView().showLoading(true);
            MediaDownloader md = new MediaDownloader(FileProcessingManager.FileType.IMAGE, new MediaDownloader.OnFileLoadCompleteListener() {
                @Override
                public void onFileLoadComplete(File result) {
                    if (isViewAttached())
                        getMvpView().setImageInstruction(getBitmap(result), result.getAbsolutePath());
                    if (isViewAttached()) getMvpView().hideLoading();
                }

                @Override
                public void onFileLoadError() {
                    if (isViewAttached()) getMvpView().hideLoading();
                }
            });
            md.getMediaFileAsync(question.getPhotoUrl());
        }


    }

    private Bitmap getBitmap(File file) {
        return SelectImageManager.prepareBitmap(file, SelectImageManager.SIZE_IN_PX_2_MP);
    }


}
