/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 05/28/2015
 * Changed 05/28/2015
 * Version 1.0.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.qiujuer.genius.blur;

import android.graphics.Bitmap;

/**
 * This use jni blur bitmap and pixels
 * Blur arithmetic is StackBlur
 */
public class StackNative {

    /**
     * Blur Image By Pixels
     *
     * @param img Img pixel array
     * @param w   Img width
     * @param h   Img height
     * @param r   Blur radius
     */
    protected static native void blurPixels(int[] img, int w, int h, int r);

    /**
     * Blur Image By Bitmap
     *
     * @param bitmap Img Bitmap
     * @param r      Blur radius
     */
    protected static native void blurBitmap(Bitmap bitmap, int r);

    /**
     * Load genius jni file
     */
    static {
        System.loadLibrary("genius_blur");
    }
}
