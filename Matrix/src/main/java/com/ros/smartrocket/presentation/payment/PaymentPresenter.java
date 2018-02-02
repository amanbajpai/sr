package com.ros.smartrocket.presentation.payment;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

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
import com.ros.smartrocket.utils.image.RequestCodeImageHelper;
import com.ros.smartrocket.utils.image.SelectImageManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class PaymentPresenter<V extends PaymentMvpView> extends BaseNetworkPresenter<V> implements PaymentMvpPresenter<V> {
    private static final String PAYMENT_PHOTO = "payment_photo";
    private boolean isImageRequested;
    private PaymentsData paymentsData;
    private File currentPhotoFile;
    private PhotoHelper photoHelper;
    private List<PaymentField> paymentFields;
    private SparseArray<File> photos = new SparseArray<>();
    private SparseArray<String> photoUrls = new SparseArray<>();

    public PaymentPresenter(PhotoHelper photoHelper) {
        this.photoHelper = photoHelper;
    }

    @Override
    public void getPaymentFields() {
        getMvpView().showLoading(false);
        photos.clear();
        photoUrls.clear();
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
            if (photos.size() != 0)
                savePaymentImages();
            else if (!paymentsData.getPaymentTextInfos().isEmpty())
                saveAllPaymentsInfo();
        } else {
            getMvpView().onPaymentFieldsNotFilled();
        }
    }

    private boolean isAllFieldsFilled() {
        return paymentFields != null && paymentFields.size() == paymentsData.getFieldsCount() && isPhotoFieldsFilled();
    }

    private boolean isPhotoFieldsFilled() {
        return paymentsData.getPaymentImageInfos() == null || isAllPhotosAdded();
    }

    private boolean isAllPhotosAdded() {
        for (PaymentInfo pi : paymentsData.getPaymentImageInfos())
            if (photos.get(pi.getPaymentFieldId()) == null && TextUtils.isEmpty(pi.getValue()))
                return false;
        return true;
    }


    private void savePaymentImages() {
        showLoading(false);
        sendFiles();
    }

    private void saveAllPaymentsInfo() {
        List<PaymentInfo> paymentInfos = getPaymentInfoList();
        if (!paymentInfos.isEmpty()) {
            showLoading(false);
            addDisposable(App.getInstance().getApi().savePaymentInfo(paymentInfos)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(rb -> onAllPaymentsSaved(), this::showNetworkError));
        } else {
            hideLoading();
        }
    }

    private List<PaymentInfo> getPaymentInfoList() {
        List<PaymentInfo> paymentInfos = new ArrayList<>();
        if (paymentsData.getPaymentTextInfos() != null)
            paymentInfos.addAll(paymentsData.getPaymentTextInfos());
        if (paymentsData.getPaymentImageInfos() != null)
            paymentInfos.addAll(getAllImagePaymentsInfos());
        return paymentInfos;
    }

    private List<PaymentInfo> getAllImagePaymentsInfos() {
        List<PaymentInfo> paymentImageInfos = new ArrayList<>();
        for (PaymentInfo pi : paymentsData.getPaymentImageInfos()) {
            String url = photoUrls.get(pi.getPaymentFieldId());
            if (!TextUtils.isEmpty(url)) {
                pi.setValue(url);
                paymentImageInfos.add(pi);
            }
        }
        return paymentImageInfos;
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

    private void sendFiles() {
        addDisposable(Observable.fromIterable(paymentsData.getPaymentImageInfos())
                .subscribeOn(Schedulers.io())
                .concatMap(this::getFileSendObservable)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        __ -> {
                        },
                        this::onFileNotUploaded,
                        this::saveAllPaymentsInfo));
    }

    private Observable<Boolean> getFileSendObservable(PaymentInfo info) {
        if (photos.get(info.getPaymentFieldId()) == null) return Observable.just(true);
        FileParser fp = new FileParser();
        NotUploadedPaymentImage notUploadedFile = getNotUploadedFile(info.getPaymentFieldId(), photos.get(info.getPaymentFieldId()));
        List<File> sendFiles = fp.getFileChunks(photos.get(info.getPaymentFieldId()), notUploadedFile);
        return getFileSendingMultipartObservable(sendFiles, notUploadedFile, fp);
    }

    private Observable<Boolean> getFileSendingMultipartObservable(List<File> sendFiles, NotUploadedPaymentImage notUploadedFile, FileParser parser) {
        Log.e("UPLOAD MULTIPART", "START SEND.");
        return Observable.fromIterable(sendFiles)
                .subscribeOn(Schedulers.io())
                .concatMap(f -> getUploadFileObservable(f, notUploadedFile, parser)
                        .doOnError(this::onFileNotUploaded)
                        .flatMap(r -> updateNotUploadedFile(r, notUploadedFile)))
                .doOnComplete(() -> onFileUploadSuccess(notUploadedFile, parser))
                .doOnError(this::onFileNotUploaded);
    }

    private Observable<Boolean> updateNotUploadedFile(FileToUploadResponse response, NotUploadedPaymentImage file) {
        Log.e("UPLOAD", "Updating main file - portion " + file.getPortion());
        file.setPortion(file.getPortion() + 1);
        file.setFileCode(response.getFileCode());
        FilesBL.updatePortionAndFileCode(file.getId(), file.getPortion(), file.getFileCode());
        photoUrls.put(file.getPaymentFieldId(), response.getFileUrl());
        return Observable.just(true);
    }

    private void onFileUploadSuccess(BaseNotUploadedFile notUploadedFile, FileParser parser) {
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
                    int fieldId = RequestCodeImageHelper.getBigPart(event.requestCode);
                    photos.put(fieldId, event.image.imageFile);
                    getMvpView().setBitmap(event.image.bitmap, fieldId);
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
    public void onPhotoClicked(String url, int fieldId) {
        if (photos.get(fieldId) != null) {
            photoHelper.showFullScreenImage(photos.get(fieldId).getPath());
        } else if (!TextUtils.isEmpty(url)) {
            photoHelper.showFullScreenImage(url);
        } else {
            onPhotoRequested(fieldId);
        }
    }

    @Override
    public void onPhotoRequested(int fieldId) {
        isImageRequested = true;
        currentPhotoFile = photoHelper.getTempFile(PAYMENT_PHOTO);
        photoHelper.showSelectImageDialog(false, currentPhotoFile, fieldId);
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

    private NotUploadedPaymentImage getNotUploadedFile(int id, File file) {
        NotUploadedPaymentImage fileToUpload = new NotUploadedPaymentImage();
        fileToUpload.setRandomId();
        fileToUpload.setPaymentFieldId(id);
        fileToUpload.setFileUri(file.getPath());
        fileToUpload.setFileSizeB(file.length());
        fileToUpload.setFileName(file.getName());
        fileToUpload.setPortion(0);
        return fileToUpload;
    }
}
