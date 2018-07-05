package ru.jevil.profitest.download;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import ru.jevil.profitest.cache.BitmapCache;
import ru.jevil.profitest.pojo.Response;

public class BitmapWorkerTask extends AsyncTask<String, Void, Response> {

    private BitmapDownloadCallback callback;
    private BitmapCache mBitmapCache;

    public BitmapWorkerTask(BitmapDownloadCallback callback) {
        mBitmapCache = BitmapCache.getInstance();
        this.callback = callback;
    }

    @Override
    protected Response doInBackground(String... params) {
        if (isCancelled()) return null;
        Bitmap bitmap;
        Response response;
        try {
            URL url = new URL(params[0]);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();
            bitmap = BitmapFactory.decodeStream(in);
            mBitmapCache.addBitmapToMemory(params[0], bitmap);
            response = new Response(bitmap, null);
        } catch (IOException e) {
            response = new Response(null, e);
        }

        return response;
    }

    @Override
    protected void onPostExecute(Response response) {
        super.onPostExecute(response);

        try {
            if (response.getException() == null)
                callback.onSuccess(response.getBitmap());
            else
                callback.onError(response.getException());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }
}