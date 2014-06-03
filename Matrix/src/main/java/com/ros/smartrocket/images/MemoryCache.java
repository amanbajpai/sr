package com.ros.smartrocket.images;

import android.graphics.Bitmap;
import com.ros.smartrocket.utils.L;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MemoryCache {

    private static final String TAG = "MemoryCache";
    private static final int CAPACITY = 10;
    private static final float LOAD_FACTOR = 1.5f;
    private static final int PART_OF_HEAP_SIZE = 5;
    private static final int BYTE_IN_KBYTE = 1024;
    private Map<String, Bitmap> cache = Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>(CAPACITY, LOAD_FACTOR, true));

    // Last argument true for LRU ordering
    private long size = 0; //current allocated size
    private long limit = 1000000; //max memory in bytes

    public MemoryCache() {
        setLimit(Runtime.getRuntime().maxMemory() / PART_OF_HEAP_SIZE);
    }

    public void setLimit(long newLimit) {
        limit = newLimit;
        L.i(TAG, "MemoryCache will use up to " + limit / BYTE_IN_KBYTE / BYTE_IN_KBYTE + "MB");
    }

    public Bitmap get(String id) {
        Bitmap result = null;
        try {
            if (cache.containsKey(id)) {
                // NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78
                result = cache.get(id);
            }
        } catch (NullPointerException e) {
            L.e(TAG, "Get bitmap error: " + e.getMessage(), e);
        }
        return result;
    }

    public void put(String id, Bitmap bitmap) {
        try {
            if (cache.containsKey(id)) {
                size -= getSizeInBytes(cache.get(id));
            }
            cache.put(id, bitmap);
            size += getSizeInBytes(bitmap);
            checkSize();
        } catch (Exception e) {
            L.e(TAG, "PutImageToCache error: " + e.getMessage(), e);
        }
    }

    private void checkSize() {
        L.i(TAG, "cache size=" + size + " length=" + cache.size());
        if (size > limit) {
            Iterator<Entry<String, Bitmap>> iter = cache.entrySet().iterator(); //least recently accessed item will be
            //the first one iterated
            while (iter.hasNext()) {
                Entry<String, Bitmap> entry = iter.next();
                size -= getSizeInBytes(entry.getValue());
                iter.remove();
                if (size <= limit) {
                    break;
                }
            }
            L.i(TAG, "Clean cache. New size " + cache.size());
        }
    }

    public void clear() {
        cache.clear();
    }

    long getSizeInBytes(Bitmap bitmap) {
        if (bitmap == null) {
            return 0;
        }
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

}