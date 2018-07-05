package ru.jevil.profitest.download;

import android.graphics.Bitmap;

public interface BitmapDownloadCallback {

    void onSuccess(Bitmap bitmap);

    void onError(Exception e);

}
