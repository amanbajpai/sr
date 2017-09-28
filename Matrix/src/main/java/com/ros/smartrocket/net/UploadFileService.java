package com.ros.smartrocket.net;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.annimon.stream.Stream;
import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.db.bl.FilesBL;
import com.ros.smartrocket.db.bl.WaitingUploadTaskBL;
import com.ros.smartrocket.db.entity.FileToUploadMultipart;
import com.ros.smartrocket.db.entity.FileToUploadResponse;
import com.ros.smartrocket.db.entity.NotUploadedFile;
import com.ros.smartrocket.db.entity.SendTaskId;
import com.ros.smartrocket.db.entity.WaitingUploadTask;
import com.ros.smartrocket.map.location.MatrixLocationManager;
import com.ros.smartrocket.net.helper.MyTaskFetcher;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.NotificationUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.SendTaskIdMapper;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.utils.eventbus.UploadProgressEvent;
import com.ros.smartrocket.utils.helpers.FileParser;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class UploadFileService extends Service {
    private static final String TAG = UploadFileService.class.getSimpleName();
    public static final int CHECK_NOT_UPLOADED_FILE_PERIOD = 600;
    public static final int NOTIFICATION_NOT_UPLOADED_FILE_PERIOD = 300;
    public static final int INITIAL_DELAY = 5;
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private Disposable uploadFilesDisposable;
    private Disposable showNotificationsDisposable;
    private Disposable waitingTaskDisposable;
    private CompositeDisposable compositeDisposable;
    private boolean uploadingFiles = false;

    private static final int MINUTE_IN_MILLISECONDS_15 = 1000 * 60 * 15;
    private static final int MINUTE_IN_MILLISECONDS_30 = 1000 * 60 * 30;
    private static final int MINUTE_IN_MILLISECONDS_60 = 1000 * 60 * 60;

    public UploadFileService() {
    }

    @Override
    public void onCreate() {
        L.i(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && Keys.ACTION_CHECK_NOT_UPLOADED_FILES.equals(intent.getAction())) {
            startCheckNotUploadedFilesTimer();
            startNotificationTimer();
        }
        return START_STICKY;
    }

    public void startCheckNotUploadedFilesTimer() {
        if (uploadFilesDisposable != null) uploadFilesDisposable.dispose();
        uploadFilesDisposable =
                Observable
                        .interval(INITIAL_DELAY, CHECK_NOT_UPLOADED_FILE_PERIOD, TimeUnit.SECONDS, Schedulers.io())
                        .subscribe(__ ->
                        {
                            if (!uploadingFiles && canUploadNextFile(UploadFileService.this))
                                getFirstFileForUpload(0);
                        }, this::onError);
    }

    private void startNotificationTimer() {
        if (showNotificationsDisposable != null) showNotificationsDisposable.dispose();
        showNotificationsDisposable =
                Observable.interval(INITIAL_DELAY, NOTIFICATION_NOT_UPLOADED_FILE_PERIOD, TimeUnit.SECONDS, Schedulers.io())
                        .subscribe(__ -> getNotUploadedFilesFroNotification(), this::onError);
    }

    private void startWaitingTaskTimer() {
        if (waitingTaskDisposable != null) waitingTaskDisposable.dispose();
        waitingTaskDisposable =
                Observable
                        .interval(INITIAL_DELAY, CHECK_NOT_UPLOADED_FILE_PERIOD, TimeUnit.SECONDS, Schedulers.io())
                        .subscribe(__ -> getWaitingTasksFromDB(), this::onError);
    }

    private void getFirstFileForUpload(long id) {
        Log.e("UPLOAD", "Get first start");
        addDisposable(FilesBL.firstNotUploadedFileObservable(id, UIUtils.is3G(UploadFileService.this))
                .observeOn(Schedulers.computation())
                .subscribe(this::onNotUploadedFileLoadedFromDb, this::onError));
    }

    private void onNotUploadedFileLoadedFromDb(NotUploadedFile notUploadedFile) {
        if (notUploadedFile.getId() != null) {
            L.i("UPLOAD", "Not uploaded found");
            updateUploadProgress(notUploadedFile);
            uploadingFiles = true;
            sendFile(notUploadedFile);
        } else {
            uploadingFiles = false;
            getCountOfNotUploadedFiles();
        }
    }

    private void sendFile(NotUploadedFile notUploadedFile) {
        FileParser fp = new FileParser();
        List<File> sendFiles = fp.getFileChunks(notUploadedFile);
        if (sendFiles != null)
            startFileSendingMultipart(sendFiles, notUploadedFile, fp);
        else
            deleteNotUploadedFileFromDb(notUploadedFile.getId());
    }

    private void startFileSending(List<File> sendFiles, NotUploadedFile notUploadedFile, FileParser parser) {
        Log.e("UPLOAD", "START SEND.");
        addDisposable(Observable.fromIterable(sendFiles)
                .observeOn(Schedulers.io())
                .concatMap(f -> App.getInstance().getApi().sendFile(parser.getFileToUpload(f, notUploadedFile))
                        .doOnError(t -> onFileNotUploaded(notUploadedFile, t, parser))
                        .flatMap(r -> updateNotUploadedFile(r, notUploadedFile)))
                .subscribe(
                        __ -> {},
                        t -> onFileNotUploaded(notUploadedFile, t, parser),
                        () -> finalizeUploading(notUploadedFile, parser)));
    }

    private void startFileSendingMultipart(List<File> sendFiles, NotUploadedFile notUploadedFile, FileParser parser) {
        Log.e("UPLOAD MULTIPART", "START SEND.");
        addDisposable(Observable.fromIterable(sendFiles)
                .observeOn(Schedulers.io())
                .concatMap(f -> getUploadFileObservable(f, notUploadedFile, parser)
                        .doOnError(t -> onFileNotUploaded(notUploadedFile, t, parser))
                        .flatMap(r -> updateNotUploadedFile(r, notUploadedFile)))
                .subscribe(
                        __ -> {},
                        t -> onFileNotUploaded(notUploadedFile, t, parser),
                        () -> finalizeUploading(notUploadedFile, parser)));
    }

    private Observable<FileToUploadResponse> getUploadFileObservable(File f, NotUploadedFile notUploadedFile, FileParser parser) {
        FileToUploadMultipart ftu = parser.getFileToUploadMultipart(f, notUploadedFile);
        RequestBody jsonBody = RequestBody.create(MediaType.parse("multipart/form-data"), ftu.getJson());
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), ftu.getFileBody());
        return App.getInstance().getApi().sendFileMultiPart(jsonBody, fileBody);
    }

    private Observable<Boolean> updateNotUploadedFile(FileToUploadResponse response, NotUploadedFile file) {
        Log.e("UPLOAD", "Updating main file - portion " + file.getPortion());
        file.setPortion(file.getPortion() + 1);
        file.setFileCode(response.getFileCode());
        FilesBL.updatePortionAndFileCode(file.getId(), file.getPortion(), file.getFileCode());
        return Observable.just(true);
    }

    private void finalizeUploading(NotUploadedFile notUploadedFile, FileParser parser) {
        Log.e("UPLOAD", "FINALIZE");
        if (parser != null) parser.cleanFiles();
        preferencesManager.setUsed3GUploadMonthlySize(preferencesManager.getUsed3GUploadMonthlySize()
                + (int) (notUploadedFile.getFileSizeB() / 1024));
        deleteNotUploadedFileFromDb(notUploadedFile.getId());
        int notUploadedFileCount = FilesBL.getNotUploadedFileCount(notUploadedFile.getTaskId(), notUploadedFile.getMissionId());
        Log.e("UPLOAD", "Not uploaded files count " + notUploadedFileCount);
        if (notUploadedFileCount == 0) {
            WaitingUploadTaskBL
                    .updateStatusToAllFileSent(notUploadedFile.getWaveId(), notUploadedFile.getTaskId(), notUploadedFile.getMissionId());
            startWaitingTaskTimer();
            validateTask(notUploadedFile);
        }
        checkForNext(notUploadedFile);
    }

    private void onFileNotUploaded(NotUploadedFile notUploadedFile, Throwable t, FileParser parser) {
        Log.e("UPLOAD", "FAILED");
        NetworkError networkError = new NetworkError(t);
        switch (networkError.getErrorCode()) {
            case NetworkError.FILE_NOT_FOUND:
                finalizeUploading(notUploadedFile, parser);
                break;
            case NetworkError.FILE_ALREADY_UPLOADED_ERROR_CODE:
                deleteNotUploadedFileFromDb(notUploadedFile.getId());
                break;
            default:
                new Handler(Looper.getMainLooper()).post(() -> UIUtils.showSimpleToast(UploadFileService.this, networkError.getErrorMessageRes()));
                break;
        }
        checkForNext(notUploadedFile);
    }

    private void checkForNext(NotUploadedFile notUploadedFile) {
        if (UIUtils.isOnline(UploadFileService.this) && canUploadNextFile(this))
            getFirstFileForUpload(notUploadedFile.get_id());
        else
            uploadingFiles = false;
    }

    private void deleteNotUploadedFileFromDb(Integer fileId) {
        FilesBL.deleteNotUploadedFileFromDbById(fileId);
    }

    private void getNotUploadedFilesFroNotification() {
        addDisposable(FilesBL.notUploadedFilesObservable()
                .observeOn(Schedulers.computation())
                .subscribe(this::showNotifications, this::onError));
    }

    private void showNotifications(List<NotUploadedFile> notUploadedFileList) {
        if (!notUploadedFileList.isEmpty())
            Stream.of(notUploadedFileList)
                    .filter(this::needSendNotification)
                    .forEach(this::updateShowNotificationStep);
        else
            stopShowNotificationTimer();
    }

    private void updateShowNotificationStep(NotUploadedFile notUploadedFile) {
        NotificationUtils.startFileNotUploadedNotificationActivity(UploadFileService.this,
                notUploadedFile.getTaskName());
        FilesBL.updateShowNotificationStep(notUploadedFile);
    }

    private void getWaitingTasksFromDB() {
        addDisposable(WaitingUploadTaskBL.waitingTasksObservable()
                .observeOn(Schedulers.computation())
                .subscribe(this::validateWaitingTasks, this::onError));
    }

    private void validateWaitingTasks(List<WaitingUploadTask> waitingUploadTasks) {
        Stream.of(waitingUploadTasks).forEach(UploadFileService.this::validateTask);
    }

    private void getCountOfNotUploadedFiles() {
        addDisposable(FilesBL.notNotUploadedFilesCountObservable()
                .observeOn(Schedulers.computation())
                .subscribe(this::onNotUploadedFilesCount, this::onError));
    }

    private void onNotUploadedFilesCount(int count) {
        if (count == 0) stopUploadedFilesTimer();
    }

    private void validateTask(final NotUploadedFile notUploadedFile) {
        Location location = new Location(LocationManager.NETWORK_PROVIDER);
        location.setLatitude(notUploadedFile.getLatitudeToValidation());
        location.setLongitude(notUploadedFile.getLongitudeToValidation());
        SendTaskId sendTaskId = SendTaskIdMapper.getSendTaskIdForValidation(notUploadedFile, location);
        getLocationAndValidate(sendTaskId, location);
    }

    private void validateTask(final WaitingUploadTask waitingUploadTask) {
        Location location = new Location(LocationManager.NETWORK_PROVIDER);
        location.setLatitude(waitingUploadTask.getLatitudeToValidation());
        location.setLongitude(waitingUploadTask.getLongitudeToValidation());
        SendTaskId sendTaskId = SendTaskIdMapper.getSendTaskIdForValidation(waitingUploadTask, location);
        getLocationAndValidate(sendTaskId, location);
    }

    private void getLocationAndValidate(SendTaskId sendTaskId, Location location) {
        MatrixLocationManager.getAddressByLocation(location, (location1, countryName, cityName, districtName) -> {
            sendTaskId.setCityName(cityName);
            sendValidateTaskRequest(sendTaskId);
        });
    }

    private void sendValidateTaskRequest(SendTaskId sendTaskId) {
        addDisposable(App.getInstance().getApi()
                .validateTask(sendTaskId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> deleteWaitingTask(sendTaskId), t -> onTaskOnValidationFailed(t, sendTaskId)));
    }

    private void onTaskOnValidationFailed(Throwable t, SendTaskId sendTaskId) {
        if (new NetworkError(t).getErrorCode() == NetworkError.TASK_NOT_FOUND_ERROR_CODE)
            deleteWaitingTask(sendTaskId);
    }

    private void deleteWaitingTask(SendTaskId sendTaskId) {
        WaitingUploadTaskBL
                .deleteUploadedTaskFromDbById(sendTaskId.getWaveId(), sendTaskId.getTaskId(), sendTaskId.getMissionId());
        new MyTaskFetcher().getMyTasksFromServer();
        updateUploadProgress(null);
    }

    private boolean needSendNotification(NotUploadedFile notUploadedFile) {
        boolean result = false;
        long currentTimeInMillis = Calendar.getInstance().getTimeInMillis();
        switch (NotUploadedFile.NotificationStepId.getStep(notUploadedFile.getShowNotificationStepId())) {
            case NONE:
                result = currentTimeInMillis > notUploadedFile.getAddedToUploadDateTime() + MINUTE_IN_MILLISECONDS_15;
                break;
            case MIN_15:
                result = currentTimeInMillis >= notUploadedFile.getAddedToUploadDateTime() + MINUTE_IN_MILLISECONDS_30;
                break;
            case MIN_30:
                result = currentTimeInMillis >= notUploadedFile.getAddedToUploadDateTime() + MINUTE_IN_MILLISECONDS_60;
                break;
            case MIN_60:
                result = false;
                break;
            default:
                break;
        }
        return result;
    }

    public static boolean canUploadNextFile(Context context) {
        PreferencesManager preferencesManager = PreferencesManager.getInstance();
        return (UIUtils.is3G(context) && !preferencesManager.getUseOnlyWiFiConnaction()) || UIUtils.isWiFi(context);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        stopShowNotificationTimer();
        stopUploadedFilesTimer();
        stopWaitingTaskTimer();
        unDispose();
        super.onDestroy();
    }

    private void onError(Throwable t) {
        Log.e(TAG, "Error on TaskReminderService", t);
    }

    public void stopUploadedFilesTimer() {
        if (uploadFilesDisposable != null) uploadFilesDisposable.dispose();
    }

    public void stopShowNotificationTimer() {
        if (showNotificationsDisposable != null) showNotificationsDisposable.dispose();
    }

    public void stopWaitingTaskTimer() {
        if (waitingTaskDisposable != null) waitingTaskDisposable.dispose();
    }

    protected void addDisposable(Disposable disposable) {
        if (compositeDisposable == null) compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(disposable);
    }

    private void unDispose() {
        if (compositeDisposable != null) compositeDisposable.clear();
    }

    private void updateUploadProgress(final NotUploadedFile notUploadedFile) {
        if (notUploadedFile != null) {
            WaitingUploadTask task = WaitingUploadTaskBL.getWaitingUploadTask(notUploadedFile.getWaveId(), notUploadedFile.getTaskId(), notUploadedFile.getMissionId());
            int notUploadedFileCount = FilesBL.getNotUploadedFileCount(notUploadedFile.getTaskId(), notUploadedFile.getMissionId());
            preferencesManager.saveUploadFilesProgress(task, notUploadedFileCount);
        } else {
            preferencesManager.clearUploadFilesProgress();
        }
        new Handler(Looper.getMainLooper()).post(() -> EventBus.getDefault().post(new UploadProgressEvent(notUploadedFile == null)));
    }
}