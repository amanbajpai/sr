package com.ros.smartrocket.presentation.question.instruction;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.ros.smartrocket.App;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.bl.CustomFieldImageUrlBL;
import com.ros.smartrocket.db.entity.question.Answer;
import com.ros.smartrocket.db.entity.question.CustomFieldImageUrls;
import com.ros.smartrocket.db.entity.question.Question;
import com.ros.smartrocket.presentation.details.claim.MediaDownloader;
import com.ros.smartrocket.presentation.question.base.BaseQuestionPresenter;
import com.ros.smartrocket.utils.FileProcessingManager;
import com.ros.smartrocket.utils.PhotoLoader;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.utils.image.SelectImageManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public void onCustomFieldImageURlLoadedFromDb(List<CustomFieldImageUrls> customFieldImageUrls) {
        super.onCustomFieldImageURlLoadedFromDb(customFieldImageUrls);
        ArrayList<String> gallery_images_list = new ArrayList<>();
        if (question.getCustomFieldImages() != null) {
            if (question.getCustomFieldImages().size() > 0) {
                for (int i = 0; i < question.getCustomFieldImages().size(); i++) {
                    gallery_images_list.add(question.getCustomFieldImages().get(i).getImageUrl());
                }
            }
        }

//        if (customFieldImageUrls != null) {
//            if (customFieldImageUrls.size() > 0) {
//                for (int i = 0; i < customFieldImageUrls.size(); i++) {
//                    gallery_images_list.add(customFieldImageUrls.get(i).getImageUrl());
////                }
//                }
//            }
//        }
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
                    file = SelectImageManager.saveBitmapToFile(App.getInstance(), bitmap, "");
                    if (isViewAttached()) {
                        getMvpView().setImageInstruction(bitmap, file.getPath().toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                if (isViewAttached()) {
                    getMvpView().hideLoading();
                    UIUtils.showSimpleToast(App.getInstance(), R.string.invalid_url);
                }
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
        ArrayList<String> gallery_images_list = new ArrayList<>();
        gallery_images_list = getCustomFieldImagesUrlFromDB();
        return gallery_images_list;
    }

    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    private ArrayList<String> getCustomFieldImagesUrlFromDB() {
        List<CustomFieldImageUrls> customFieldImageUrls = new ArrayList<>();
        customFieldImageUrls = CustomFieldImageUrlBL.convertCursorToCustomFieldImageUrlList(CustomFieldImageUrlBL.getCustomFiledImageUrlListFromDB(question));

        ArrayList<String> gallery_images_list = new ArrayList<>();
        if (customFieldImageUrls != null) {
            if (customFieldImageUrls.size() > 0) {
                for (int i = 0; i < customFieldImageUrls.size(); i++) {
                    gallery_images_list.add(customFieldImageUrls.get(i).getImageUrl());
                }
            }
        }

        return gallery_images_list;
    }

}
