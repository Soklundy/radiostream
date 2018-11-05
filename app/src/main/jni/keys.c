//
// Created by Lenovo on 7/28/2017.
//

#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_hm_rhm_radiostream_activity_MainActivity_getHmTv(JNIEnv *env, jobject instance) {

    return (*env)->NewStringUTF(env,
                                "http://malisresidencess.com:1934/redirect/hm_hdtv/smil:HMHDTV.smil?scheme=m3u8");
}

JNIEXPORT jstring JNICALL
Java_com_hm_rhm_radiostream_activity_MainActivity_getRhmTv(JNIEnv *env, jobject instance) {

    return (*env)->NewStringUTF(env,
                                "http://malisresidencess.com:1934/redirect/rhm_hdtv/smil:RHMHDTV.smil?scheme=m3u8");
}

JNIEXPORT jstring JNICALL
Java_com_hm_rhm_radiostream_activity_MainActivity_getRhmRadio(JNIEnv *env, jobject instance) {

    return (*env)->NewStringUTF(env, "http://malisresidencess.com:90/broadwavehigh.mp3?src=1");
}

JNIEXPORT jstring JNICALL
Java_com_hm_rhm_radiostream_activity_MainActivity_getHmRadio(JNIEnv *env, jobject instance) {

    return (*env)->NewStringUTF(env, "http://malisresidencess.com:89/broadwavehigh.mp3?src=1");
}