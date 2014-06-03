package com.ros.smartrocket.images;


import com.ros.smartrocket.App;
import com.ros.smartrocket.Config;

import java.io.File;

public class FileCache {

    private File cacheDir;

    public FileCache() {
        // Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(Config.CACHE_DIR, "images");
        } else {
            cacheDir = App.getInstance().getCacheDir();
        }
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
    }

    public File getFile(String url) {
        // I identify images by hashcode. Not a perfect solution, good for the demo.

        String filename = String.valueOf(url.hashCode());
        return new File(cacheDir, filename);

    }

    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            f.delete();
        }
    }

    public File getCacheDir() {
        return cacheDir;
    }
}
