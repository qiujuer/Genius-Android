/*
 * Copyright (C) 2014-2016 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Author qiujuer
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

#include <android/log.h>
#include <android/bitmap.h>
#include "stackblur.h"

#define TAG "net.qiujuer.genius.graphics.Blur"
#define LOG_D(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__)


JNIEXPORT void JNICALL
Java_net_qiujuer_genius_graphics_Blur_nativeStackBlurPixels(JNIEnv *env, jclass type,
                                                            jintArray pixels_, jint w, jint h,
                                                            jint r) {
    // get it
    jint *pixels = (*env)->GetIntArrayElements(env, pixels_, NULL);

    // check
    if (pixels == NULL) {
        LOG_D("Input pixels isn't null.");
        return;
    }

    // blur
    pixels = blur_ARGB_8888(pixels, w, h, r);


    // release
    (*env)->ReleaseIntArrayElements(env, pixels_, pixels, 0);


    // cpp:
    /*
    jint *pixels = (env)->GetIntArrayElements(pixels_, 0);
    jintArray result = env->NewIntArray(w * h);
    env->SetIntArrayRegion(result, 0, w * h, pixels);
    (env)->ReleaseIntArrayElements(arrIn, pix, 0);
    return result;
    */
}

JNIEXPORT void JNICALL
Java_net_qiujuer_genius_graphics_Blur_nativeStackBlurBitmap(JNIEnv *env, jclass type,
                                                            jobject bitmap, jint r) {

    AndroidBitmapInfo infoIn;
    void *pixels;

    // Get bitmap info
    if (AndroidBitmap_getInfo(env, bitmap, &infoIn) != ANDROID_BITMAP_RESULT_SUCCESS) {
        LOG_D("AndroidBitmap_getInfo failed!");
        return;
    }

    // Check bitmap
    if (infoIn.format != ANDROID_BITMAP_FORMAT_RGBA_8888 &&
        infoIn.format != ANDROID_BITMAP_FORMAT_RGB_565) {
        LOG_D("Only support ANDROID_BITMAP_FORMAT_RGBA_8888 and ANDROID_BITMAP_FORMAT_RGB_565");
        return;
    }

    // Lock the bitmap
    if (AndroidBitmap_lockPixels(env, bitmap, &pixels) != ANDROID_BITMAP_RESULT_SUCCESS) {
        LOG_D("AndroidBitmap_lockPixels failed!");
        return;
    }

    // Size
    int h = infoIn.height;
    int w = infoIn.width;

    // Blur
    if (infoIn.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
        pixels = blur_ARGB_8888((int *) pixels, w, h, r);
    } else if (infoIn.format == ANDROID_BITMAP_FORMAT_RGB_565) {
        pixels = blur_RGB_565((short *) pixels, w, h, r);
    }

    // Unlocks everything
    AndroidBitmap_unlockPixels(env, bitmap);

}