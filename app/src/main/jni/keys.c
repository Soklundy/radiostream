//
// Created by Lenovo on 7/28/2017.
//

#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_hm_rhm_radiostream_activity_MainActivity_getHmTv(JNIEnv *env, jobject instance) {

    return (*env)->NewStringUTF(env,
                                "http://111.92.240.134:1934/hm_hdtv/smil:HMHDTV.smil/playlist.m3u8");
}

JNIEXPORT jstring JNICALL
Java_com_hm_rhm_radiostream_activity_MainActivity_getRhmTv(JNIEnv *env, jobject instance) {

    return (*env)->NewStringUTF(env,
                                "http://111.92.240.134:1934/rhm_hdtv/smil:RHMHDTV.smil/playlist.m3u8");
}

JNIEXPORT jstring JNICALL
Java_com_hm_rhm_radiostream_activity_MainActivity_getRhmRadio(JNIEnv *env, jobject instance) {

    return (*env)->NewStringUTF(env, "http://111.92.240.134:90/broadwavehigh.mp3");
}

JNIEXPORT jstring JNICALL
Java_com_hm_rhm_radiostream_activity_MainActivity_getHmRadio(JNIEnv *env, jobject instance) {

    return (*env)->NewStringUTF(env, "http://111.92.240.134:89/broadwavehigh.mp3");
}