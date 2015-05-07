package com.ros.smartrocket.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import com.ros.smartrocket.BuildConfig;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class FileProcessingManager {
    private static final String TAG = FileProcessingManager.class.getSimpleName();
    public static final String CACHE_PREFIX_DIR = "/Android/data/" + BuildConfig.APPLICATION_ID + "/";

    // private static int PADDING = 80;

    public enum FileType {
        IMAGE("images", "png"),
        VIDEO("videos", "mp4"),
        HTML("web_pages", "html"),
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

    public File createSubDir(File baseDir, String newDirName) {
        File newDir = new File(baseDir, newDirName);

        if (!newDir.exists()) {
            newDir.mkdirs();
        }
        return newDir;
    }

    public File getTempFile(FileType fileType, String fileName, boolean useExternalStorage) {
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

    public File getRawRsourceAsFile(Activity activity, int resId, File fileToSave) {
        if (!fileToSave.exists()) {
            InputStream is;
            OutputStream os;
            try {
                is = activity.getResources().openRawResource(resId);
                os = new FileOutputStream(fileToSave);
                copyStream(is, os);
                os.close();
            } catch (Exception e) {
                L.e(TAG, "Error copyRsourceToFile: " + e);
            }
        }

        return fileToSave;
    }

    public File saveBitmapToFile(Bitmap bitmap, String fileName, FileType fileType) {
        File resultFile = getTempFile(fileType, fileName, true);

        try {
            FileOutputStream fos = new FileOutputStream(resultFile, false);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

            fos.flush();
            fos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return resultFile;
    }

    public File saveStringToFile(String data, String fileName, FileType fileType) {
        File resultFile = getTempFile(fileType, fileName, true);

        try {
            OutputStream fos = new FileOutputStream(resultFile);
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

    public void saveStreamToFile(InputStream is, File file) {
        try {
            OutputStream os = new FileOutputStream(file);

            copyStream(is, os);
            is.close();
            os.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void copyAndCloseStream(InputStream is, OutputStream os) {
        try {
            copyStream(is, os);
            is.close();
            os.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getStringFromFile(File file) {
        StringBuilder result = new StringBuilder();
        try {
            InputStream is = new FileInputStream(file);
            BufferedReader r = new BufferedReader(new InputStreamReader(is));

            String line;

            while ((line = r.readLine()) != null) {
                result.append(line);
            }

            is.close();
        } catch (Exception e) {
            L.e(TAG, "Error getStringFromFile: " + file.getAbsolutePath());
        }
        return result.toString();
    }

    public void getFileByUrl(String url, FileType fileType, OnLoadFileListener loadFileListener) {
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

        protected void onPostExecute(File file) {
            if (loadFileListener != null) {
                if (resultFile != null) {
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

    public void copyStream(InputStream is, OutputStream os) {
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

    /*public Bitmap getBitmapByPath(String path, ImageSizeType sizeType) {
        if (!TextUtils.isEmpty(path)) {
            return decodeFile(new File(path), sizeType);
        } else {
            return BitmapFactory.decodeResource(App.getInstance().getResources(), R.drawable.no_widget_photo);
        }
    }*/
}
