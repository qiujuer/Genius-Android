package net.qiujuer.genius.app;

import android.graphics.Bitmap;

/**
 * Created by QiuJu
 * on 2014/11/25.
 * <p/>
 * This is call native JNI class
 */
class BlurNative {

    /**
     * Blur Image By Pixels
     *
     * @param img Img pixel array
     * @param w   Img width
     * @param h   Img height
     * @param r   Blur radius
     */
    public static native void blurPixels(int[] img, int w, int h, int r);

    /**
     * Blur Image By Bitmap
     *
     * @param bitmap Img Bitmap
     * @param r      Blur radius
     */
    public static native void blurBitmap(Bitmap bitmap, int r);

    /**
     * Load genius jni file
     */
    static {
        System.loadLibrary("genius");
    }
}
