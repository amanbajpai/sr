package com.ros.smartrocket.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.StorageManager;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.File;
import java.io.FileFilter;

public class CleanFilesIntentService extends IntentService {
    public static final String EXTRA_PARAM_PREFIX = "com.ros.smartrocket.service.extra.PARAM_PREFIX";

    public CleanFilesIntentService() {
        super("CleanFilesIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String prefix = intent.getStringExtra(EXTRA_PARAM_PREFIX);
            L.v("PREFIX", prefix);

            File dir = StorageManager.getImageStoreDir(this);
            FileFilter fileFilter = new WildcardFileFilter(prefix + "_*_*.jpg");
            File[] files = dir.listFiles(fileFilter);
            for (File file : files) {
                L.v("FILE TO DELETE", file.toString());
                file.delete();
            }
        }
    }

    public static void start(Context context, String prefix) {
        Intent intent = new Intent(context, CleanFilesIntentService.class);
        intent.putExtra(EXTRA_PARAM_PREFIX, prefix);
        context.startService(intent);
    }
}