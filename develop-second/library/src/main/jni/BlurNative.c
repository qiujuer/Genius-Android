/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 12/25/2014
 * Changed 12/25/2014
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

/*************************************************
Copyright:  Copyright QIUJUER 2014.
Author:		QiuJu
Date:		2014-11-26
Description:Realize image blurred images blurred
**************************************************/
#include <BlurNative.h>
#include <malloc.h>
#include <jni.h>
#include <android/bitmap.h>

#define ABS(a) ((a)<(0)?(-a):(a))
#define MAX(a,b) ((a)>(b)?(a):(b))
#define MIN(a,b) ((a)<(b)?(a):(b))

/*************************************************
Function:		StackBlur
Description:    Using stack way blurred image pixels
Calls:          malloc
Table Accessed: NULL
Table Updated:	NULL
Input:          Collection of pixels, wide image, image is high, the blur radius
Output:         After return to fuzzy collection of pixels
Return:         After return to fuzzy collection of pixels
Others:         NULL
*************************************************/
int* stackBlur(int* pix, int w, int h, int radius) {
	int wm = w - 1;
	int hm = h - 1;
	int wh = w * h;
	int div = radius + radius + 1;

	int *r = (int *)malloc(wh * sizeof(int));
	int *g = (int *)malloc(wh * sizeof(int));
	int *b = (int *)malloc(wh * sizeof(int));
	int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;

	int *vmin = (int *)malloc(MAX(w, h) * sizeof(int));

	int divsum = (div + 1) >> 1;
	divsum *= divsum;
	int *dv = (int *)malloc(256 * divsum * sizeof(int));
	for (i = 0; i < 256 * divsum; i++) {
		dv[i] = (i / divsum);
	}

	yw = yi = 0;

	int(*stack)[3] = (int(*)[3])malloc(div * 3 * sizeof(int));
	int stackpointer;
	int stackstart;
	int *sir;
	int rbs;
	int r1 = radius + 1;
	int routsum, goutsum, boutsum;
	int rinsum, ginsum, binsum;

	for (y = 0; y < h; y++) {
		rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
		for (i = -radius; i <= radius; i++) {
			p = pix[yi + (MIN(wm, MAX(i, 0)))];
			sir = stack[i + radius];
			sir[0] = (p & 0xff0000) >> 16;
			sir[1] = (p & 0x00ff00) >> 8;
			sir[2] = (p & 0x0000ff);

			rbs = r1 - ABS(i);
			rsum += sir[0] * rbs;
			gsum += sir[1] * rbs;
			bsum += sir[2] * rbs;
			if (i > 0) {
				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];
			}
			else {
				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];
			}
		}
		stackpointer = radius;

		for (x = 0; x < w; x++) {

			r[yi] = dv[rsum];
			g[yi] = dv[gsum];
			b[yi] = dv[bsum];

			rsum -= routsum;
			gsum -= goutsum;
			bsum -= boutsum;

			stackstart = stackpointer - radius + div;
			sir = stack[stackstart % div];

			routsum -= sir[0];
			goutsum -= sir[1];
			boutsum -= sir[2];

			if (y == 0) {
				vmin[x] = MIN(x + radius + 1, wm);
			}
			p = pix[yw + vmin[x]];

			sir[0] = (p & 0xff0000) >> 16;
			sir[1] = (p & 0x00ff00) >> 8;
			sir[2] = (p & 0x0000ff);

			rinsum += sir[0];
			ginsum += sir[1];
			binsum += sir[2];

			rsum += rinsum;
			gsum += ginsum;
			bsum += binsum;

			stackpointer = (stackpointer + 1) % div;
			sir = stack[(stackpointer) % div];

			routsum += sir[0];
			goutsum += sir[1];
			boutsum += sir[2];

			rinsum -= sir[0];
			ginsum -= sir[1];
			binsum -= sir[2];

			yi++;
		}
		yw += w;
	}
	for (x = 0; x < w; x++) {
		rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
		yp = -radius * w;
		for (i = -radius; i <= radius; i++) {
			yi = MAX(0, yp) + x;

			sir = stack[i + radius];

			sir[0] = r[yi];
			sir[1] = g[yi];
			sir[2] = b[yi];

			rbs = r1 - ABS(i);

			rsum += r[yi] * rbs;
			gsum += g[yi] * rbs;
			bsum += b[yi] * rbs;

			if (i > 0) {
				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];
			}
			else {
				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];
			}

			if (i < hm) {
				yp += w;
			}
		}
		yi = x;
		stackpointer = radius;
		for (y = 0; y < h; y++) {
			// Preserve alpha channel: ( 0xff000000 & pix[yi] )
			pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

			rsum -= routsum;
			gsum -= goutsum;
			bsum -= boutsum;

			stackstart = stackpointer - radius + div;
			sir = stack[stackstart % div];

			routsum -= sir[0];
			goutsum -= sir[1];
			boutsum -= sir[2];

			if (x == 0) {
				vmin[y] = MIN(y + r1, hm) * w;
			}
			p = x + vmin[y];

			sir[0] = r[p];
			sir[1] = g[p];
			sir[2] = b[p];

			rinsum += sir[0];
			ginsum += sir[1];
			binsum += sir[2];

			rsum += rinsum;
			gsum += ginsum;
			bsum += binsum;

			stackpointer = (stackpointer + 1) % div;
			sir = stack[stackpointer];

			routsum += sir[0];
			goutsum += sir[1];
			boutsum += sir[2];

			rinsum -= sir[0];
			ginsum -= sir[1];
			binsum -= sir[2];

			yi += w;
		}
	}

	free(r);
	free(g);
	free(b);
	free(vmin);
	free(dv);
	free(stack);
	return(pix);
}


JNIEXPORT void JNICALL Java_net_qiujuer_genius_app_BlurNative_blurPixels
(JNIEnv *env, jclass obj, jintArray arrIn, jint w, jint h, jint r)
{
	jint *pix;
	// cpp:
	// pix = (env)->GetIntArrayElements(arrIn, 0);
	pix = (*env)->GetIntArrayElements(env, arrIn, 0);
	if (pix == NULL)
		return;
	// Start
	pix = stackBlur(pix, w, h, r);
	// End

	// if return:
	// int size = w * h;
	// jintArray result = env->NewIntArray(size);
	// env->SetIntArrayRegion(result, 0, size, pix);
	// cpp:
	// (env)->ReleaseIntArrayElements(arrIn, pix, 0);
	(*env)->ReleaseIntArrayElements(env, arrIn, pix, 0);
	// return result;
}

JNIEXPORT void JNICALL Java_net_qiujuer_genius_app_BlurNative_blurBitmap
(JNIEnv *env, jclass obj, jobject bitmapIn, jint r)
{
	AndroidBitmapInfo infoIn;
	void* pixelsIn;
	int ret;

	// Get image info
	if ((ret = AndroidBitmap_getInfo(env, bitmapIn, &infoIn)) != 0)
		return;
	// Check image
	if (infoIn.format != ANDROID_BITMAP_FORMAT_RGBA_8888)
		return;
	// Lock all images
	if ((ret = AndroidBitmap_lockPixels(env, bitmapIn, &pixelsIn)) != 0) {
		//AndroidBitmap_lockPixels failed!
		return;
	}
	// height width
	int h = infoIn.height;
	int w = infoIn.width;

	// Start
	pixelsIn = stackBlur((int*)pixelsIn, w, h, r);
	// End

	// Unlocks everything
	AndroidBitmap_unlockPixels(env, bitmapIn);
}