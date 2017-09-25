package com.ros.smartrocket.utils.helpers;

import android.net.Uri;
import android.util.Log;

import com.annimon.stream.Stream;
import com.ros.smartrocket.App;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.db.entity.FileToUpload;
import com.ros.smartrocket.db.entity.NotUploadedFile;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.image.SelectImageManager;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class FileParser {
    private static int MAX_BYTE_SIZE = 1000 * 16 * 1000;
    private List<File> files;
    private long mainFileLength;

    public FileParser() {
        if (!Config.USE_BAIDU) {
            MAX_BYTE_SIZE = 32 * 1000;
        }
    }

    public List<File> getFileChunks(NotUploadedFile notUploadedFile) {
        try {
            File sourceFile = new File(Uri.parse(notUploadedFile.getFileUri()).getPath());
            if (!sourceFile.exists() || sourceFile.length() == 0)
                writeNoImageToSourceFile(sourceFile);
            mainFileLength = sourceFile.length();
            files = separateFile(notUploadedFile);
            Log.e("UPLOAD", "Files - " + files.size());
            return files;
        } catch (IOException e) {
            cleanFiles();
            return null;
        }
    }

    public FileToUpload getFileToUpload(File file, NotUploadedFile notUploadedFile) {
        Log.e("UPLOAD", "GetFileTU  Portion - < " + notUploadedFile.getPortion());
        FileToUpload uploadFileEntity = new FileToUpload();
        uploadFileEntity.setTaskId(notUploadedFile.getTaskId());
        uploadFileEntity.setMissionId(notUploadedFile.getMissionId());
        uploadFileEntity.setQuestionId(notUploadedFile.getQuestionId());
        uploadFileEntity.setFileOffset((long) MAX_BYTE_SIZE * notUploadedFile.getPortion());
        uploadFileEntity.setFileCode(notUploadedFile.getFileCode());
        uploadFileEntity.setFilename(notUploadedFile.getFileName());
        uploadFileEntity.setFileLength(mainFileLength);
        uploadFileEntity.setFileBase64String(SelectImageManager.getFileAsString(file));
        uploadFileEntity.setLanguageCode(PreferencesManager.getInstance().getLanguageCode());
        return uploadFileEntity;
    }

    private List<File> separateFile(NotUploadedFile notUploadedFile) throws IOException {
        File sourceFile = new File(Uri.parse(notUploadedFile.getFileUri()).getPath());
        byte[] sourceByteArray = FileUtils.readFileToByteArray(sourceFile);
        int portionCount = (int) Math.ceil((double) sourceByteArray.length / MAX_BYTE_SIZE);
        if (notUploadedFile.getPortion() == portionCount) {
            notUploadedFile.setPortion(0);
            notUploadedFile.setFileCode(null);
        }
        List<File> files = Arrays.asList(new File[portionCount - notUploadedFile.getPortion()]);
        for (int i = notUploadedFile.getPortion(); i < portionCount; i++) {
            int startPosition = MAX_BYTE_SIZE * i;
            int length = MAX_BYTE_SIZE;
            if (startPosition + length > sourceByteArray.length)
                length = sourceByteArray.length - startPosition;
            byte[] tempByteArray = new byte[length];
            System.arraycopy(sourceByteArray, startPosition, tempByteArray, 0, length);
            File tempFile = SelectImageManager.getTempFile(App.getInstance(), notUploadedFile.getTaskId().toString());
            FileOutputStream fos = new FileOutputStream(tempFile, false);
            fos.write(tempByteArray);
            fos.close();
            files.set(i - notUploadedFile.getPortion(), tempFile);
        }
        return files;
    }

    public void cleanFiles() {
        if (files != null)
            Stream.of(files).forEach(FileUtils::deleteQuietly);
    }

    private void writeNoImageToSourceFile(File sourceFile) throws IOException {
        InputStream inputStream = App.getInstance().getAssets().open("images/no_image5.jpg");
        FileUtils.copyInputStreamToFile(inputStream, sourceFile);
    }

}
