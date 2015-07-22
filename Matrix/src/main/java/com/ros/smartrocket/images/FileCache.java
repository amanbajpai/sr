package com.ros.smartrocket.images;


import com.ros.smartrocket.App;
import com.ros.smartrocket.utils.StorageManager;

import java.io.File;

public class FileCache {

    private File cacheDir;

    /**
     * Create cache directory
     */
    public FileCache() {
        // Find the dir to save cached images
        cacheDir = StorageManager.getImageCacheDir(App.getInstance());
    }

    /**
     * Make file in cache directory
     */
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
