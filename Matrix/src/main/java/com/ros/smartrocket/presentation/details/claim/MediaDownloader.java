package com.ros.smartrocket.presentation.details.claim;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.ros.smartrocket.db.bl.QuestionsBL;
import com.ros.smartrocket.db.entity.question.Category;
import com.ros.smartrocket.db.entity.question.Product;
import com.ros.smartrocket.db.entity.question.Question;
import com.ros.smartrocket.utils.FileProcessingManager;
import com.ros.smartrocket.utils.image.SelectImageManager;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public final class MediaDownloader {
    private OnFileLoadCompleteListener fileListener;
    private OnImageLoadCompleteListener imageLoadCompleteListener;
    private FileProcessingManager.FileType type = FileProcessingManager.FileType.OTHER;

    public MediaDownloader(FileProcessingManager.FileType type, OnFileLoadCompleteListener fileListener) {
        this.type = type;
        this.fileListener = fileListener;
    }

    public MediaDownloader(OnImageLoadCompleteListener listener) {
        this.imageLoadCompleteListener = listener;
    }

    private static int downloadInstructionQuestionFile(List<Question> questions) {
        int filesDownloadedCount = 0;
        for (Question question : questions) {
            String fileUrl = "";
            FileProcessingManager.FileType fileType = null;

            if (!TextUtils.isEmpty(question.getPhotoUrl())) {
                fileUrl = question.getPhotoUrl();
                fileType = FileProcessingManager.FileType.IMAGE;
            } else if (!TextUtils.isEmpty(question.getVideoUrl())) {
                fileUrl = question.getVideoUrl();
                fileType = FileProcessingManager.FileType.VIDEO;
            }

            if (!fileUrl.isEmpty() && fileType != null) {
                File resultFile = FileProcessingManager.getTempFile(fileType, null, true);
                try {
                    resultFile = downloadFileSync(fileUrl, resultFile);
                    QuestionsBL.updateInstructionFileUri(question.getWaveId(), question.getTaskId(),
                            question.getMissionId(), question.getId(), resultFile.getPath());
                    filesDownloadedCount++;
                } catch (Exception e) {
                    Log.e("MediaDownloader", "Can't load file - " + fileUrl, e);
                }
            }
        }
        return filesDownloadedCount;
    }

    private static int downloadMassAuditProductFile(List<Question> questions) {
        int filesDownloadedCount = 0;
        for (Question question : questions) {
            for (Category category : question.getCategoriesArray()) {
                filesDownloadedCount += loadCategoryImage(category) ? 1 : 0;
                if (category.getProducts() != null)
                    for (Product product : category.getProducts())
                        filesDownloadedCount += loadProductImage(product) ? 1 : 0;
            }
            QuestionsBL.updateQuestionCategories(question.getWaveId(), question.getTaskId(),
                    question.getMissionId(), question.getId(), new Gson().toJson(question.getCategoriesArray()));
        }
        return filesDownloadedCount;
    }

    private static boolean loadProductImage(final Product product) {
        if (!TextUtils.isEmpty(product.getImage()))
            try {
                File resultFile = FileProcessingManager.getTempFile(FileProcessingManager.FileType.IMAGE, null, true);
                resultFile = downloadFileSync(product.getImage(), resultFile);
                product.setCachedImage(resultFile.getAbsolutePath());
                return true;
            } catch (IOException e) {
                Log.e("MediaDownloader", "Can't load file - " + product.getImage(), e);
            }
        return false;
    }

    private static boolean loadCategoryImage(Category category) {
        if (!TextUtils.isEmpty(category.getImage()))
            try {
                File resultFile = FileProcessingManager.getTempFile(FileProcessingManager.FileType.IMAGE, null, true);
                resultFile = downloadFileSync(category.getImage(), resultFile);
                category.setCachedImage(resultFile.getAbsolutePath());
                return true;
            } catch (IOException e) {
                Log.e("MediaDownloader", "Can't load file - " + category.getImage(), e);
            }
        return false;
    }


    private static File downloadFileSync(String downloadUrl, File file) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(downloadUrl).build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Failed to download file: " + response);
        }
        FileUtils.copyInputStreamToFile(response.body().byteStream(), file);
        return file;
    }

    static Observable<Integer> getDownloadInstructionQuestionsObservable(List<Question> questions) {
        return Observable.fromCallable(() -> downloadInstructionQuestionFile(questions));
    }

    static Observable<Integer> getDownloadMassAuditProductFileObservable(List<Question> questions) {
        return Observable.fromCallable(() -> downloadMassAuditProductFile(questions));
    }

    public interface OnFileLoadCompleteListener {
        void onFileLoadComplete(File result);

        void onFileLoadError();
    }

    public interface OnImageLoadCompleteListener {
        void onImageLoadComplete(Bitmap result);

        void onImageLoadError();
    }

    public void getMediaFileAsync(String url) {
        Observable.fromCallable(() -> getFile(url))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onComplete, this::onError);
    }

    private void onComplete(File file) {
        if (fileListener != null) fileListener.onFileLoadComplete(file);
    }

    private void onError(Throwable t) {
        if (fileListener != null) fileListener.onFileLoadError();
        Log.e("MediaDownloader", "Video loading error", t);
    }

    private File getFile(String fileUrl) throws IOException {
        File resultFile = FileProcessingManager.getTempFile(type, fileUrl, true);
        if (resultFile.exists())
            return resultFile;
        resultFile = downloadFileSync(fileUrl, resultFile);
        return resultFile;
    }

    public void loadImageAsync(String url) {
        Observable.fromCallable(() -> getImage(url))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onImageLoadingComplete, this::onImageLoadingError);
    }

    private Bitmap getImage(String fileUrl) throws IOException {
        File resultFile = FileProcessingManager.getTempFile(type, fileUrl, true);
        if (resultFile.exists())
            return getBitmapFromFile(resultFile);
        resultFile = downloadFileSync(fileUrl, resultFile);
        return getBitmapFromFile(resultFile);
    }

    private Bitmap getBitmapFromFile(File f) {
        return SelectImageManager.prepareBitmap(f, SelectImageManager.SIZE_IN_PX_2_MP);
    }

    private void onImageLoadingComplete(Bitmap bitmap) {
        if (imageLoadCompleteListener != null)
            imageLoadCompleteListener.onImageLoadComplete(bitmap);
    }

    private void onImageLoadingError(Throwable t) {
        if (imageLoadCompleteListener != null) imageLoadCompleteListener.onImageLoadError();
        Log.e("MediaDownloader", "Video loading error", t);
    }
}
