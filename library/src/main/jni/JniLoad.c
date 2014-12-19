#include <stdlib.h>
#include <jni.h> 

JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void *reserved)
{
	JNIEnv* env = NULL;
	jint result = -1;

	if ((*vm)->GetEnv(vm, (void**)&env, JNI_VERSION_1_4) != JNI_OK)
		return -1;

	result = JNI_VERSION_1_4;
	return result;
}