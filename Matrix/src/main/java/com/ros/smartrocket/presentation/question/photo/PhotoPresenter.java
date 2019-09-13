package com.ros.smartrocket.presentation.question.photo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
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


    public PhotoPresenter(Question question, PhotoHelper photoHelper) {
        super(question);
        this.photoQuestionHelper = photoHelper;
    }

    @Override
    public void onAnswersLoadedFromDb(List<Answer> answers) {
        if (answers.isEmpty()) addEmptyAnswer();
        super.onAnswersLoadedFromDb(answers);
        if (!isBitmapAdded)
            selectGalleryPhoto(0);
    }

    @Override
    public void onPhotoEvent(PhotoEvent event) {
        switch (event.type) {
            case START_LOADING:
                getMvpView().showLoading(false);
                break;
            case IMAGE_COMPLETE:
                if (event.requestCode == null
                        || RequestCodeImageHelper.getBigPart(event.requestCode) == question.getOrderId()) {
                    lastPhotoFile = event.image.imageFile;
                    isBitmapAdded = event.image.bitmap != null;
                    isBitmapConfirmed = false;
                    getMvpView().setBitmap(event.image.bitmap);
                    getMvpView().hideLoading();
                    if (event.image.bitmap != null)
                        onPhotoConfirmed(getMvpView().getCurrentPos());

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
    public void onPhotoConfirmed(int photoPos) {
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
                    saveAnswer(location, photoPos);
                    PreferencesManager.getInstance().setBoolean(Keys.IS_COMPRESS_PHOTO,true);

                }
            }

            @Override
            public void getLocationFail(String errorText) {
                if (isViewAttached()) {
                    getMvpView().hideLoading();
                    UIUtils.showSimpleToast(App.getInstance(), errorText);
                    PreferencesManager.getInstance().setBoolean(Keys.IS_COMPRESS_PHOTO,true);
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
            getMvpView().refreshPhotoGallery(question.getAnswers());
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
            PreferencesManager.getInstance().setBoolean(Keys.IS_COMPRESS_PHOTO,question.getCompressionphoto());
            onPhotoRequested(photoPos);
        }
    }

    @Override
    public void onPhotoRequested(int photoPos) {
        if (question.getPhotoSource() == 0) {
            mCurrentPhotoFile = photoQuestionHelper.getTempFile(question.getTaskId().toString());
            photoQuestionHelper.startCamera(mCurrentPhotoFile, question.getOrderId());
        } else if (question.getPhotoSource() == 1) {
            //anil
            //photoQuestionHelper.startGallery(question.getOrderId());
            photoQuestionHelper.openGallery(question.getOrderId(),0);
        } else {
            mCurrentPhotoFile = photoQuestionHelper.getTempFile(question.getTaskId().toString());
            photoQuestionHelper.showSelectImageDialog(false, mCurrentPhotoFile, question.getOrderId());
        }
        isImageRequested = true;
    }

    @Override
    public void onGalleryPhotoRequested(int photoPos, int imageListsize) {
        if(question.getPhotoSource() == 0){

        }else  if(question.getPhotoSource() == 1){
            photoQuestionHelper.openGallery(question.getOrderId(),imageListsize);
        }

    }

    private void saveAnswer(Location location, int photoPos) {

        new AsyncTask<Void,Void ,File>(){

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
                    getMvpView().refreshPhotoGallery(question.getAnswers());
                    isBitmapConfirmed = true;
                    refreshButtons();
                    refreshNextButton(isPhotosAdded());
                }
                getMvpView().hideLoading();
            }
        }.execute();


    }

    //anil
    public void handleResults(File resultImageFile, String imagesStr){

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

                    if (resultImageFile.exists()) {
                        Answer answer = question.getAnswers().get(0);
                        boolean needAddEmptyAnswer = !answer.getChecked();
                        answer.setChecked(true);
                        answer.setFileUri(imagesStr);
                        answer.setFileSizeB(resultImageFile.length());
                        answer.setFileName(resultImageFile.getName());
                        answer.setValue(resultImageFile.getName());
                        answer.setLatitude(location.getLatitude());
                        answer.setLongitude(location.getLongitude());
                        if (!isPreview()) saveQuestion();
                        if (needAddEmptyAnswer && question.getAnswers().size() < question.getMaximumPhotos())
                            addEmptyAnswer();
                        getMvpView().refreshPhotoGallery(question.getAnswers());
                        isBitmapConfirmed = true;
                        refreshButtons();
                        refreshNextButton(isPhotosAdded());
                    }
                    getMvpView().hideLoading();


                    PreferencesManager.getInstance().setBoolean(Keys.IS_COMPRESS_PHOTO,true);

                }
            }

            @Override
            public void getLocationFail(String errorText) {
                if (isViewAttached()) {
                    getMvpView().hideLoading();
                    UIUtils.showSimpleToast(App.getInstance(), errorText);
                    PreferencesManager.getInstance().setBoolean(Keys.IS_COMPRESS_PHOTO,true);
                }
            }
        });
    }

    String getCommonSeperatedString(List<GalleryInfo> actionObjects) {
        StringBuffer sb = new StringBuffer();
        for (GalleryInfo actionObject : actionObjects){
            sb.append(actionObject.imagePath).append(",");
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        return sb.toString();
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent intent) {
        //anil
       /* if(resultCode == RESULT_OK && requestCode == 102){
            HashMap<String,GalleryInfo> selectedImgPath = (HashMap<String, GalleryInfo>) intent.getSerializableExtra("selectedImgPath");
            getMvpView().getSelectedImgPath(selectedImgPath);

            List<GalleryInfo> values = new ArrayList<>(selectedImgPath.values());
            String imagesStr =  getCommonSeperatedString(values);

            handleResults(new File(values.get(0).imagePath),imagesStr);
        }*/

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
