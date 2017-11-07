package com.ros.smartrocket.utils;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import com.ros.smartrocket.App;
import com.ros.smartrocket.BuildConfig;
import com.ros.smartrocket.MatrixFileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class FileProcessingManager {
    private static final String TAG = FileProcessingManager.class.getSimpleName();
    public static final String CACHE_PREFIX_DIR = "/Android/data/" + BuildConfig.APPLICATION_ID + "/";
    public final static String FILE_LOGS = "logs_result.txt";
    public static final int BYTE_IN_KB = 1024;
    public static final int MAX_FILE_SIZE_IN_MB = 2;

    // private static int PADDING = 80;

    public enum FileType {
        IMAGE("images", "png"),
        VIDEO("videos", "mp4"),
        HTML("web_pages", "html"),
        TEXT("text", "txt"),
        OTHER("others", "");

        private String folderName;

        private String defaultFileType;

        private FileType(String folderName, String fileType) {
            this.folderName = folderName;
            this.defaultFileType = fileType;
        }

        public String getFolderName() {
            return folderName;
        }

        public String getDefaultFileType() {
            return defaultFileType;
        }
    }

    private static FileProcessingManager instance = null;

    public static FileProcessingManager getInstance() {
        if (instance == null) {
            instance = new FileProcessingManager();
        }
        return instance;
    }

    public FileProcessingManager() {
    }

    public static File getCashDir(boolean useExternalStorage) {
        String state = Environment.getExternalStorageState();

        File cacheDir;
        if (useExternalStorage && Environment.MEDIA_MOUNTED.equals(state)) {
            cacheDir = Environment.getExternalStoragePublicDirectory(CACHE_PREFIX_DIR);
        } else {
            cacheDir = Environment.getDataDirectory();
        }

        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        return cacheDir;
    }

    public static File createSubDir(File baseDir, String newDirName) {
        File newDir = new File(baseDir, newDirName);

        if (!newDir.exists()) {
            newDir.mkdirs();
        }
        return newDir;
    }

    public static File getTempFile(FileType fileType, String fileName, boolean useExternalStorage) {
        File file = null;
        try {
            if (TextUtils.isEmpty(fileName)) {
                fileName = String.valueOf(Calendar.getInstance().getTimeInMillis());
                if (!TextUtils.isEmpty(fileType.getDefaultFileType())) {
                    fileName = fileName + "." + fileType.getDefaultFileType();
                }
            }

            File cashDir = getCashDir(useExternalStorage);
            File tempFileDir = createSubDir(cashDir, fileType.getFolderName());

            file = new File(tempFileDir, fileName);
        } catch (Exception e) {
            L.e(TAG, "Error get Temp File");
        }
        return file;
    }

    private static long getFileSizeInMB(File file) {
        return getFileSizeInByte(file) / BYTE_IN_KB;
    }

    private static long getFileSizeInByte(File file) {
        return file.length() / BYTE_IN_KB;
    }

    void getFileByUrl(String url, FileType fileType, OnLoadFileListener loadFileListener) {
        File resultFile = getTempFile(fileType, null, true);

        new LoadFileByUrlAsyncTask(url, resultFile, loadFileListener).execute();
    }

    private class LoadFileByUrlAsyncTask extends AsyncTask<Void, Integer, File> {
        private static final int TIMEOUT = 30000;
        private final OnLoadFileListener loadFileListener;
        private final String url;
        private final File resultFile;

        public LoadFileByUrlAsyncTask(String url, File resultFile, OnLoadFileListener loadFileListener) {
            this.loadFileListener = loadFileListener;
            this.url = url;
            this.resultFile = resultFile;
        }

        @Override
        protected void onPreExecute() {
            if (loadFileListener != null) {
                loadFileListener.onStartFileLoading();
            }
        }

        @Override
        protected File doInBackground(Void... v) {
            try {
                URL imageUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                conn.setConnectTimeout(TIMEOUT);
                conn.setReadTimeout(TIMEOUT);
                conn.setInstanceFollowRedirects(true);
                InputStream is = conn.getInputStream();
                OutputStream os = new FileOutputStream(resultFile);
                copyStream(is, os);
                os.close();
            } catch (Exception e) {
                L.e(TAG, "getFile: " + url + " " + e, e);
                return null;
            }

            return resultFile;
        }

        @Override
        protected void onPostExecute(File file) {
            if (loadFileListener != null) {
                if (file != null) {
                    loadFileListener.onFileLoaded(file);
                } else {
                    loadFileListener.onFileLoadingError();
                }
            }


        }
    }

    public static interface OnLoadFileListener {
        public void onStartFileLoading();

        public void onFileLoaded(File file);

        public void onFileLoadingError();

    }

    public static void copyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1) {
                    break;
                }
                os.write(bytes, 0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File saveStringToFile(String data, String fileName, FileType fileType, boolean append) {
        File resultFile = getTempFile(fileType, fileName, true);
        try {
            if (shouldClearFile(resultFile) && resultFile.delete()) {
                resultFile.createNewFile();
            }
            OutputStream fos = new FileOutputStream(resultFile, append);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(data);
            osw.close();

            fos.flush();
            fos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return resultFile;
    }

    private boolean shouldClearFile(File file) {
        return file.exists() && getFileSizeInMB(file) > MAX_FILE_SIZE_IN_MB;
    }

    public static Uri getUriFromFile(File file) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            return MatrixFileProvider.getUriForFile(App.getInstance(), BuildConfig.APPLICATION_ID + ".provider", file);
        } else {
            return Uri.fromFile(file);
        }
    }
}
