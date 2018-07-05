package ru.jevil.profitest.cache;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.LruCache;

public class BitmapCache {
    private LruCache<String, Bitmap> mMemoryCache;

    private BitmapCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public static BitmapCache getInstance() {
        return SingletonHolder.instance;
    }

    public Bitmap getBitmapFromMemory(String url) {
        return mMemoryCache.get(url);
    }

    public void addBitmapToMemory(String url ,Bitmap bitmap) {
        if (getBitmapFromMemory(url) == null) {
            mMemoryCache.put(url, bitmap);
        }
    }

    private static class SingletonHolder {
        private final static BitmapCache instance = new BitmapCache();
    }
}
