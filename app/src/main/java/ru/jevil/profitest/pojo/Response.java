package ru.jevil.profitest.pojo;

import android.graphics.Bitmap;

public class Response {

    private Bitmap bitmap;
    private Exception exception;

    public Response(Bitmap bitmap, Exception exception) {
        this.bitmap = bitmap;
        this.exception = exception;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
