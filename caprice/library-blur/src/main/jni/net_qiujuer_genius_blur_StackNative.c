#include <net_qiujuer_genius_blur_StackNative.h>
#include <stackblur.h>
#include <android/bitmap.h>

JNIEXPORT void JNICALL Java_net_qiujuer_genius_blur_StackNative_blurPixels
(JNIEnv *env, jclass obj, jintArray arrIn, jint w, jint h, jint r)
{
	jint *pix;
	// cpp:
	// pix = (env)->GetIntArrayElements(arrIn, 0);
	pix = (*env)->GetIntArrayElements(env, arrIn, 0);
	if (pix == NULL)
		return;
	// Start
	pix = blur(pix, w, h, r);
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

JNIEXPORT void JNICALL Java_net_qiujuer_genius_blur_StackNative_blurBitmap
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
	pixelsIn = blur((int*)pixelsIn, w, h, r);
	// End

	// Unlocks everything
	AndroidBitmap_unlockPixels(env, bitmapIn);
}