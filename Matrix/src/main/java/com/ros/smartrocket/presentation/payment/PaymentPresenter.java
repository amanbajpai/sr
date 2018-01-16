package com.ros.smartrocket.presentation.payment;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.db.bl.FilesBL;
import com.ros.smartrocket.db.entity.file.FileToUploadResponse;
import com.ros.smartrocket.db.entity.file.NotUploadedFile;
import com.ros.smartrocket.db.entity.file.TaskFileToUpload;
import com.ros.smartrocket.db.entity.payment.PaymentField;
import com.ros.smartrocket.db.entity.payment.PaymentInfo;
import com.ros.smartrocket.db.entity.payment.PaymentsData;
import com.ros.smartrocket.presentation.base.BaseNetworkPresenter;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.eventbus.PhotoEvent;
import com.ros.smartrocket.utils.helpers.FileParser;
import com.ros.smartrocket.utils.helpers.photo.PhotoHelper;
import com.ros.smartrocket.utils.image.SelectImageManager;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class PaymentPresenter<V extends PaymentMvpView> extends BaseNetworkPresenter<V> implements PaymentMvpPresenter<V> {
    public static final String PAYMENT_PHOTO = "payment_photo";
    private String imagePath;
    private boolean isImageRequested;
    private PaymentsData paymentsData;
    private File lastPhotoFile;
    private PhotoHelper photoHelper;

    public PaymentPresenter(PhotoHelper photoHelper) {
        this.photoHelper = photoHelper;
    }

    @Override
    public void getPaymentFields() {
        getMvpView().showLoading(false);
        int countryId = PreferencesManager.getInstance().getInt(Keys.COUNTRY_ID, 0);
        addDisposable(App.getInstance().getApi().getPaymentFields(countryId, getLanguageCode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onPaymentFieldsLoaded, this::showNetworkError));
    }

    @Override
    public void savePaymentsInfo(PaymentsData paymentsData) {
        this.paymentsData = paymentsData;
        if (!TextUtils.isEmpty(imagePath))
            savePaymentImage();
        else if (!paymentsData.getPaymentTextInfos().isEmpty())
            saveAllPaymentsInfo(paymentsData.getPaymentTextInfos());
        else
            getMvpView().onPaymentFieldsEmpty();
    }

    private void savePaymentImage() {
        showLoading(false);
        sendFile();
    }

    private void onPaymentImageSaved(String s) {
        PaymentInfo paymentImageInfo = paymentsData.getPaymentImageInfo();
        paymentImageInfo.setValue(s);
        List<PaymentInfo> paymentInfoList = paymentsData.getPaymentTextInfos();
        paymentInfoList.add(paymentImageInfo);
        saveAllPaymentsInfo(paymentInfoList);
    }

    private void saveAllPaymentsInfo(List<PaymentInfo> paymentInfoList) {
        showLoading(false);
        addDisposable(App.getInstance().getApi().savePaymentInfo(paymentInfoList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rb -> onAllPaymentsSaved(), this::showNetworkError));
    }

    private void onAllPaymentsSaved() {
        if (isViewAttached()) {
            hideLoading();
            getMvpView().onPaymentsSaved();
        }
    }

    private void onPaymentFieldsLoaded(List<PaymentField> pf) {
        if (isViewAttached()) {
            hideLoading();
            getMvpView().onPaymentFieldsLoaded(pf);
        }
    }

    private Single<String> getSavePaymentImageSingle(String imgPath) {
        return App.getInstance().getApi().sendPaymentFile(null, null);
    }

    private void sendFile() {
        FileParser fp = new FileParser();
        NotUploadedFile notUploadedFile = getNotUploadedFile();
        List<File> sendFiles = fp.getFileChunks(lastPhotoFile, notUploadedFile);
        if (sendFiles != null)
            startFileSendingMultipart(sendFiles, notUploadedFile, fp);
    }

    private void startFileSendingMultipart(List<File> sendFiles, NotUploadedFile notUploadedFile, FileParser parser) {
        Log.e("UPLOAD MULTIPART", "START SEND.");
        addDisposable(Observable.fromIterable(sendFiles)
                .observeOn(Schedulers.io())
                .concatMap(f -> getUploadFileObservable(f, notUploadedFile, parser)
                        .doOnError(this::onFileNotUploaded)
                        .flatMap(r -> updateNotUploadedFile(r, notUploadedFile)))
                .subscribe(
                        __ -> {
                        },
                        this::onFileNotUploaded,
                        () -> onFileUploadSuccess(notUploadedFile, parser)));
    }

    private Observable<Boolean> updateNotUploadedFile(FileToUploadResponse response, NotUploadedFile file) {
        Log.e("UPLOAD", "Updating main file - portion " + file.getPortion());
        file.setPortion(file.getPortion() + 1);
        file.setFileCode(response.getFileCode());
        FilesBL.updatePortionAndFileCode(file.getId(), file.getPortion(), file.getFileCode());
        return Observable.just(true);
    }

    private void onFileUploadSuccess(NotUploadedFile notUploadedFile, FileParser parser) {
        Log.e("UPLOAD", "SUCCESS");
        if (parser != null) parser.cleanFiles();
        PreferencesManager preferencesManager = PreferencesManager.getInstance();
        preferencesManager.setUsed3GUploadMonthlySize(preferencesManager.getUsed3GUploadMonthlySize()
                + (int) (notUploadedFile.getFileSizeB() / 1024));
    }

    private void onFileNotUploaded(Throwable t) {
        if (isViewAttached()) {
            hideLoading();
            showNetworkError(t);
        }
    }

    private Observable<FileToUploadResponse> getUploadFileObservable(File f, NotUploadedFile notUploadedFile, FileParser parser) {
        TaskFileToUpload ftu = parser.getFileToUploadMultipart(f, notUploadedFile);
        RequestBody jsonBody = RequestBody.create(MediaType.parse("multipart/form-data"), ftu.getJson());
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), f);
        MultipartBody.Part fileBody =
                MultipartBody.Part.createFormData("questionFile", f.getName(), requestFile);
        return App.getInstance().getApi().sendFileMultiPart(jsonBody, fileBody);
    }

    @Override
    public void onPhotoEvent(PhotoEvent event) {
        switch (event.type) {
            case START_LOADING:
                getMvpView().showLoading(false);
                break;
            case IMAGE_COMPLETE:
                if (event.requestCode == null) {
                    lastPhotoFile = event.image.imageFile;
                    getMvpView().setBitmap(event.image.bitmap);
                    getMvpView().hideLoading();
                }
                break;
            case SELECT_IMAGE_ERROR:
                getMvpView().hideLoading();
                getMvpView().showPhotoCanNotBeAddDialog();
                break;
        }
    }

    @Override
    public void onPhotoDeleted() {
        lastPhotoFile = null;
        getMvpView().setBitmap(null);
    }

    @Override
    public void onPhotoClicked(String url) {
        if (lastPhotoFile != null) {
            photoHelper.showFullScreenImage(lastPhotoFile.getPath());
        } else if (url != null) {
            photoHelper.showFullScreenImage(url);
        }
    }

    @Override
    public void onPhotoRequested() {
        isImageRequested = true;
        photoHelper.showSelectImageDialog(PAYMENT_PHOTO);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (isImageRequested) {
            if (lastPhotoFile != null) {
                intent = new Intent();
                intent.putExtra(SelectImageManager.EXTRA_PHOTO_FILE, lastPhotoFile);
                intent.putExtra(SelectImageManager.EXTRA_PREFIX, PAYMENT_PHOTO);
                photoHelper.onActivityResult(requestCode, resultCode, intent);
                isImageRequested = false;
                return true;
            }
            if (intent != null && intent.getData() != null) {
                intent.putExtra(SelectImageManager.EXTRA_PREFIX, PAYMENT_PHOTO);
                photoHelper.onActivityResult(requestCode, resultCode, intent);
                isImageRequested = false;
                return true;
            }
        }
        return false;
    }

    private NotUploadedFile getNotUploadedFile() {
        NotUploadedFile fileToUpload = new NotUploadedFile();
        fileToUpload.setRandomId();
        fileToUpload.setFileUri(lastPhotoFile.getPath());
        fileToUpload.setFileSizeB(lastPhotoFile.length());
        fileToUpload.setFileName(lastPhotoFile.getName());
        fileToUpload.setShowNotificationStepId(0);
        fileToUpload.setPortion(0);
        return fileToUpload;
    }
}
