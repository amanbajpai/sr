package com.ros.smartrocket.presentation.question.instruction;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.entity.question.Question;
import com.ros.smartrocket.presentation.details.claim.MediaDownloader;
import com.ros.smartrocket.presentation.question.base.BaseQuestionPresenter;
import com.ros.smartrocket.utils.FileProcessingManager;
import com.ros.smartrocket.utils.PhotoLoader;
import com.ros.smartrocket.utils.image.SelectImageManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Map;

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
        PhotoLoader.getBitmapFromUrl(question.getPhotoUrl(), new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                try {
                    getMvpView().hideLoading();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                file = SelectImageManager.saveBitmapToFile(App.getInstance(), bitmap, "");
                getMvpView().setImageInstruction(bitmap, file.getPath().toString());

            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                getMvpView().showLoading(true);
            }
        });

//        if (!TextUtils.isEmpty(question.getInstructionFileUri())) {
//            File file = new File(question.getInstructionFileUri());
//            getMvpView().setImageInstruction(getBitmap(file), file.getPath());
//        } else {
//            getMvpView().showLoading(true);
//            MediaDownloader md = new MediaDownloader(FileProcessingManager.FileType.IMAGE, new MediaDownloader.OnFileLoadCompleteListener() {
//                @Override
//                public void onFileLoadComplete(File result) {
//                    if (isViewAttached())
//                        getMvpView().setImageInstruction(getBitmap(result), result.getAbsolutePath());
//                    if (isViewAttached()) getMvpView().hideLoading();
//                }
//
//                @Override
//                public void onFileLoadError() {
//                    if (isViewAttached()) getMvpView().hideLoading();
//                }
//            });
//            md.getMediaFileAsync(question.getPhotoUrl());
//        }


    }

    private Bitmap getBitmap(File file) {
        return SelectImageManager.prepareBitmap(file, SelectImageManager.SIZE_IN_PX_2_MP);
    }

    @Override
    public ArrayList<String> getDialogGalleryImages() {
//        ArrayList<String> gallery_images_list = new ArrayList<>();
//        Map<String, String> gallery_images_map = question.getTaskLocationObject().getCustomFieldsMap();
//        for (Map.Entry<String, String> entry : gallery_images_map.entrySet()) {
//            if (entry.getKey().contains("CustomField") && entry.getValue() != null) {
//                if (isImageFile(String.valueOf(Html.fromHtml(entry.getValue())))) {
//                    gallery_images_list.add(String.valueOf(Html.fromHtml(entry.getValue())));
//                }
//            }
//        }

        ArrayList<String> gallery_images_list = new ArrayList<>();
        if (question.getImagesGallery() != null) {
            if (question.getImagesGallery().size() > 0) {
                for (int i = 0; i < question.getImagesGallery().size(); i++) {
                    gallery_images_list.add(String.valueOf(Html.fromHtml(question.getImagesGallery().get(i))));
                }
            }
        }
        return gallery_images_list;
    }

    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }


}
