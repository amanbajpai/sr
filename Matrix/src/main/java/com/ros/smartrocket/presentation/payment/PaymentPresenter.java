package com.ros.smartrocket.presentation.payment;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.bl.FilesBL;
import com.ros.smartrocket.db.entity.file.BaseNotUploadedFile;
import com.ros.smartrocket.db.entity.file.FileToUploadResponse;
import com.ros.smartrocket.db.entity.file.NotUploadedPaymentImage;
import com.ros.smartrocket.db.entity.file.PaymentFileToUpload;
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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class PaymentPresenter<V extends PaymentMvpView> extends BaseNetworkPresenter<V> implements PaymentMvpPresenter<V> {
    private static final String PAYMENT_PHOTO = "payment_photo";
    private String imagePath;
    private boolean isImageRequested;
    private PaymentsData paymentsData;
    private File lastPhotoFile;
    private File currentPhotoFile;
    private PhotoHelper photoHelper;
    private List<PaymentField> paymentFields;

    public PaymentPresenter(PhotoHelper photoHelper) {
        this.photoHelper = photoHelper;
    }

    @Override
    public void getPaymentFields() {
        getMvpView().showLoading(false);
        int countryId = App.getInstance().getMyAccount().getCountryId();
        addDisposable(App.getInstance().getApi().getPaymentFields(countryId, getLanguageCode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onPaymentFieldsLoaded, this::showNetworkError));
    }

    @Override
    public void savePaymentsInfo(PaymentsData paymentsData) {
        this.paymentsData = paymentsData;
        if (isAllFieldsFilled()) {
            if (lastPhotoFile != null)
                savePaymentImage();
            else if (!paymentsData.getPaymentTextInfos().isEmpty())
                saveAllPaymentsInfo(paymentsData.getPaymentTextInfos());
        } else {
            getMvpView().onPaymentFieldsNotFilled();
        }
    }

    private boolean isAllFieldsFilled() {
        return paymentFields != null && paymentFields.size() == paymentsData.getFieldsCount() && isPhotoFieldFilled();
    }

    private boolean isPhotoFieldFilled() {
        return paymentsData.getPaymentImageInfo() == null || lastPhotoFile != null;
    }


    private void savePaymentImage() {
        showLoading(false);
        sendFile();
    }

    private void onPaymentImageSaved() {
        List<PaymentInfo> paymentInfoList = paymentsData.getPaymentTextInfos();
        if (!TextUtils.isEmpty(imagePath)) {
            PaymentInfo paymentImageInfo = paymentsData.getPaymentImageInfo();
            paymentImageInfo.setValue(imagePath);
            paymentInfoList.add(paymentImageInfo);
            imagePath = null;
        }
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
        paymentFields = pf;
        if (isViewAttached()) {
            hideLoading();
            if (pf != null && !pf.isEmpty())
                getMvpView().onPaymentFieldsLoaded(pf);
            else
                getMvpView().onPaymentFieldsEmpty();
        }
    }

    private void sendFile() {
        FileParser fp = new FileParser();
        NotUploadedPaymentImage notUploadedFile = getNotUploadedFile(paymentsData.getPaymentImageInfo().getPaymentFieldId());
        List<File> sendFiles = fp.getFileChunks(lastPhotoFile, notUploadedFile);
        if (sendFiles != null)
            startFileSendingMultipart(sendFiles, notUploadedFile, fp);
    }

    private void startFileSendingMultipart(List<File> sendFiles, NotUploadedPaymentImage notUploadedFile, FileParser parser) {
        Log.e("UPLOAD MULTIPART", "START SEND.");
        addDisposable(Observable.fromIterable(sendFiles)
                .subscribeOn(Schedulers.io())
                .concatMap(f -> getUploadFileObservable(f, notUploadedFile, parser)
                        .doOnError(this::onFileNotUploaded)
                        .flatMap(r -> updateNotUploadedFile(r, notUploadedFile)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        __ -> {
                        },
                        this::onFileNotUploaded,
                        () -> onFileUploadSuccess(notUploadedFile, parser)));
    }

    private Observable<Boolean> updateNotUploadedFile(FileToUploadResponse response, BaseNotUploadedFile file) {
        Log.e("UPLOAD", "Updating main file - portion " + file.getPortion());
        file.setPortion(file.getPortion() + 1);
        file.setFileCode(response.getFileCode());
        FilesBL.updatePortionAndFileCode(file.getId(), file.getPortion(), file.getFileCode());
        imagePath = response.getFileUrl();
        return Observable.just(true);
    }

    private void onFileUploadSuccess(BaseNotUploadedFile notUploadedFile, FileParser parser) {
        Log.e("UPLOAD", "SUCCESS");
        if (parser != null) parser.cleanFiles();
        PreferencesManager preferencesManager = PreferencesManager.getInstance();
        preferencesManager.setUsed3GUploadMonthlySize(preferencesManager.getUsed3GUploadMonthlySize()
                + (int) (notUploadedFile.getFileSizeB() / 1024));
        onPaymentImageSaved();
    }

    private void onFileNotUploaded(Throwable t) {
        if (isViewAttached()) {
            hideLoading();
            showNetworkError(t);
        }
    }

    private Observable<FileToUploadResponse> getUploadFileObservable(File f, NotUploadedPaymentImage notUploadedFile, FileParser parser) {
        PaymentFileToUpload ftu = parser.getPaymentFileToUploadMultipart(f, notUploadedFile);
        RequestBody jsonBody = RequestBody.create(MediaType.parse("multipart/form-data"), ftu.getJson());
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), f);
        MultipartBody.Part fileBody =
                MultipartBody.Part.createFormData("questionFile", f.getName(), requestFile);
        return App.getInstance().getApi().sendPaymentFile(jsonBody, fileBody);
    }

    @Override
    public void onPhotoEvent(PhotoEvent event) {
        switch (event.type) {
            case START_LOADING:
                getMvpView().showLoading(false);
                break;
            case IMAGE_COMPLETE:
                if (event.image != null) {
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
    public void onPhotoClicked(String url) {
        if (lastPhotoFile != null) {
            photoHelper.showFullScreenImage(lastPhotoFile.getPath());
        } else if (!TextUtils.isEmpty(url)) {
            photoHelper.showFullScreenImage(url);
        } else {
            onPhotoRequested();
        }
    }

    @Override
    public void onPhotoRequested() {
        isImageRequested = true;
        currentPhotoFile = photoHelper.getTempFile(PAYMENT_PHOTO);
        photoHelper.showSelectImageDialog(false, currentPhotoFile);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (isImageRequested) {
            if (intent != null && intent.getData() != null) {
                intent.putExtra(SelectImageManager.EXTRA_PREFIX, PAYMENT_PHOTO);
                photoHelper.onActivityResult(requestCode, resultCode, intent);
                isImageRequested = false;
                return true;
            }
            if (currentPhotoFile != null) {
                intent = new Intent();
                intent.putExtra(SelectImageManager.EXTRA_PHOTO_FILE, currentPhotoFile);
                intent.putExtra(SelectImageManager.EXTRA_PREFIX, PAYMENT_PHOTO);
                photoHelper.onActivityResult(requestCode, resultCode, intent);
                isImageRequested = false;
                return true;
            }
        }
        return false;
    }

    private NotUploadedPaymentImage getNotUploadedFile(int id) {
        NotUploadedPaymentImage fileToUpload = new NotUploadedPaymentImage();
        fileToUpload.setRandomId();
        fileToUpload.setPaymentFieldId(id);
        fileToUpload.setFileUri(lastPhotoFile.getPath());
        fileToUpload.setFileSizeB(lastPhotoFile.length());
        fileToUpload.setFileName(lastPhotoFile.getName());
        fileToUpload.setPortion(0);
        return fileToUpload;
    }
}
