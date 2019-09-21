package com.ros.smartrocket.presentation.question.photo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.db.entity.question.Answer;
import com.ros.smartrocket.db.entity.question.Question;
import com.ros.smartrocket.map.location.MatrixLocationManager;
import com.ros.smartrocket.presentation.question.base.BaseQuestionPresenter;
import com.ros.smartrocket.ui.gallery.model.GalleryInfo;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.utils.eventbus.PhotoEvent;
import com.ros.smartrocket.utils.helpers.photo.PhotoHelper;
import com.ros.smartrocket.utils.image.RequestCodeImageHelper;
import com.ros.smartrocket.utils.image.SelectImageManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class PhotoPresenter<V extends PhotoMvpView> extends BaseQuestionPresenter<V> implements PhotoMvpPresenter<V> {
    private File mCurrentPhotoFile;
    private File lastPhotoFile;
    private boolean isBitmapAdded = false;
    private boolean isBitmapConfirmed = false;
    private boolean isImageRequested;
    private PhotoHelper photoQuestionHelper;
    private int aQuesCount;


    public PhotoPresenter(Question question, PhotoHelper photoHelper) {
        super(question);
        this.photoQuestionHelper = photoHelper;
    }

    @Override
    public void onAnswersLoadedFromDb(List<Answer> answers) {
        if (answers.isEmpty()) addEmptyAnswer();
        super.onAnswersLoadedFromDb(answers);
        if (!isBitmapAdded)
            selectGalleryPhoto(1);
    }

    @Override
    public void onPhotoEvent(PhotoEvent event) {
        switch (event.type) {
            case START_LOADING:
                getMvpView().showLoading(false);
                break;
            case IMAGE_COMPLETE:
                //from Camera
                if (event.requestCode == null || RequestCodeImageHelper.getBigPart(event.requestCode) == question.getOrderId()) {
                    lastPhotoFile = event.image.imageFile;
                    isBitmapAdded = event.image.bitmap != null;
                    isBitmapConfirmed = false;
                    getMvpView().hideLoading();
                    if (event.image.bitmap != null) {
                        getMvpView().setBitmap(event.image.bitmap);
                        onPhotoConfirmed(getMvpView().getCurrentPos(), 1);
                    } else {
                        // from Gallery
                        ArrayList<File> mSelectedFileList = event.image.mSelectedFileList;
                        getMvpView().hideLoading();
                        isBitmapAdded = true;
                        isBitmapConfirmed = false;
                        if (mSelectedFileList.size() > 0) {
                            getMvpView().getSelectedImgPath(mSelectedFileList);
                            for (int j = 0; j < mSelectedFileList.size(); j++) {
                                lastPhotoFile = mSelectedFileList.get(j).getAbsoluteFile();
                                onPhotoConfirmed(j , 2);
                            }
                        }
                    }
                    refreshButtons();
                    refreshNextButton(isPhotosAdded());
                }
                break;
            case SELECT_IMAGE_ERROR:
                getMvpView().hideLoading();
                getMvpView().showPhotoCanNotBeAddDialog();
                break;
        }
    }

    @Override
    public void selectGalleryPhoto(int position) {
        Answer answer = question.getAnswers().get(position);
        if (answer.getChecked() && answer.getFileUri() != null) {
            isBitmapAdded = true;
            isBitmapConfirmed = true;
            Bitmap bitmap = SelectImageManager.prepareBitmap(new File(answer.getFileUri()));
            getMvpView().selectGalleryPhoto(position);
            getMvpView().setBitmap(bitmap);
            getMvpView().setImagePath(answer.getFileUri());
            refreshButtons();
        } else {
            isBitmapAdded = false;
            isBitmapConfirmed = false;
            getMvpView().selectGalleryPhoto(position);
            getMvpView().setBitmap(null);
            refreshButtons();
        }
        refreshNextButton(isPhotosAdded());
    }

    private void refreshButtons() {
        getMvpView().refreshRePhotoButton(isBitmapAdded);
        getMvpView().refreshDeletePhotoButton(isBitmapAdded);
        getMvpView().refreshConfirmButton(isBitmapConfirmed);
    }

    @Override
    public void onPhotoConfirmed(int photoPos , int type) {
        MatrixLocationManager.getCurrentLocation(false, new MatrixLocationManager
                .GetCurrentLocationListener() {
            @Override
            public void getLocationStart() {
                getMvpView().showLoading(false);
            }

            @Override
            public void getLocationInProcess() {
            }

            @Override
            public void getLocationSuccess(Location location) {
                if (isViewAttached()) {
                    getMvpView().hideLoading();
                    saveAnswer(location, photoPos ,type );
                    PreferencesManager.getInstance().setBoolean(Keys.IS_COMPRESS_PHOTO, true);

                }
            }

            @Override
            public void getLocationFail(String errorText) {
                if (isViewAttached()) {
                    getMvpView().hideLoading();
                    UIUtils.showSimpleToast(App.getInstance(), errorText);
                    PreferencesManager.getInstance().setBoolean(Keys.IS_COMPRESS_PHOTO, true);
                }
            }
        });
    }

    @Override
    public void onPhotoDeleted(int photoPos) {
        if (isBitmapConfirmed) {
            if (question.getAnswers().size() > photoPos)
                deleteAnswer(question.getAnswers().get(photoPos));
        } else {
            isBitmapAdded = false;
            refreshButtons();
            getMvpView().refreshPhotoGallery(question.getAnswers() , 0);
            getMvpView().setBitmap(null);
        }
    }

    @Override
    public void onPhotoClicked(int photoPos) {
        if (isBitmapAdded) {
            String filePath = "";
            if (!isBitmapConfirmed) {
                filePath = lastPhotoFile.getPath();
            } else if (question.getAnswers().size() > photoPos) {
                Answer answer = question.getAnswers().get(photoPos);
                filePath = answer.getFileUri();
            }
            photoQuestionHelper.showFullScreenImage(filePath);
        } else {
            PreferencesManager.getInstance().setBoolean(Keys.IS_COMPRESS_PHOTO, question.getCompressionphoto());
            onPhotoRequested(photoPos);
        }
    }

    @Override
    public void onPhotoRequested(int photoPos) {
        if (question.getPhotoSource() == 0) {
            mCurrentPhotoFile = photoQuestionHelper.getTempFile(question.getTaskId().toString());
            photoQuestionHelper.startCamera(mCurrentPhotoFile, question.getOrderId());
        } else if (question.getPhotoSource() == 1) {
            photoQuestionHelper.openGallery(question.getOrderId(), aQuesCount);
        } else {
            mCurrentPhotoFile = photoQuestionHelper.getTempFile(question.getTaskId().toString());
            photoQuestionHelper.showSelectImageDialog(false, mCurrentPhotoFile, question.getOrderId());
        }
        isImageRequested = true;
    }

    @Override
    public void onQuestionCount(int aCount) {

        aQuesCount = aCount;
    }

    @Override
    public void onGalleryPhotoRequested(int photoPos, int imageListsize) {
        if (question.getPhotoSource() == 0) {

        } else if (question.getPhotoSource() == 1) {
            photoQuestionHelper.openGallery(question.getOrderId(), imageListsize);
        }

    }

    private void saveAnswer(Location location, int photoPos, int type) {

        new AsyncTask<Void, Void, File>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                getMvpView().showLoading(false);
            }

            @Override
            protected File doInBackground(Void... voids) {
                File file = SelectImageManager.getScaledFile(lastPhotoFile, SelectImageManager.SIZE_IN_PX_2_MP);

                return file;
            }

            @Override
            protected void onPostExecute(File resultImageFile) {
                super.onPostExecute(resultImageFile);
                if (resultImageFile.exists() && question.getAnswers().size() > photoPos) {
                    Answer answer = question.getAnswers().get(photoPos);
                     boolean needAddEmptyAnswer = !answer.getChecked();
                    answer.setChecked(true);
                    answer.setFileUri(Uri.fromFile(resultImageFile).getPath());
                    answer.setFileSizeB(resultImageFile.length());
                    answer.setFileName(resultImageFile.getName());
                    answer.setValue(resultImageFile.getName());
                    answer.setLatitude(location.getLatitude());
                    answer.setLongitude(location.getLongitude());
                    if (!isPreview()) saveQuestion();
                    if (needAddEmptyAnswer && question.getAnswers().size() < question.getMaximumPhotos())
                        addEmptyAnswer();
                    for (int i = 0; i < question.getAnswers().size(); i++) {
                        Log.e("Richa0" , ""+question.getAnswers().get(i).getFileUri());
                    }
                    getMvpView().refreshPhotoGallery(question.getAnswers() , type);
                    isBitmapConfirmed = true;
                    refreshButtons();
                    refreshNextButton(isPhotosAdded());
                }
                getMvpView().hideLoading();
            }
        }.execute();
    }


    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (isImageRequested) {
            if (mCurrentPhotoFile != null) {
                intent = new Intent();
                intent.putExtra(SelectImageManager.EXTRA_PHOTO_FILE, mCurrentPhotoFile);
                intent.putExtra(SelectImageManager.EXTRA_PREFIX, question.getTaskId().toString());
                photoQuestionHelper.onActivityResult(requestCode, resultCode, intent);
                isImageRequested = false;
                return true;
            } else if (intent != null) { //anil
                intent.putExtra(SelectImageManager.EXTRA_PREFIX, question.getTaskId().toString());
                photoQuestionHelper.onActivityResult(requestCode, resultCode, intent);
                isImageRequested = false;
                return true;
            }
        }
        return false;
    }

    @Override
    public void onAnswersDeleted() {
        if (question.getAnswers().size() == question.getMaximumPhotos() && !isLastAnswerEmpty())
            addEmptyAnswer();
        isBitmapAdded = false;
        getMvpView().setCurrentPos(0);
        loadAnswers();
    }

    @Override
    public void onAnswersUpdated() {
        if (isViewAttached())
            getMvpView().hideLoading();
    }

    private boolean isPhotosAdded() {
        for (Answer answer : question.getAnswers()) {
            if (!TextUtils.isEmpty(answer.getFileUri()) && answer.getChecked()) return true;
        }
        return false;
    }

    private boolean isLastAnswerEmpty() {
        int lastPos = question.getAnswers().size() - 1;
        return TextUtils.isEmpty(question.getAnswers().get(lastPos).getFileUri());
    }
}
