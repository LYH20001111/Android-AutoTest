/**
 * @Description
 * @Author wuhh
 * @Date 2021/6/17
 */
#include <log.h>
#include <string.h>
#include <jni.h>
#include "lodepng.h"

JNIEXPORT jint JNICALL
Java_com_newland_intelligent_jni_JniCmdInterface_encodeOneStep(JNIEnv *env, jobject thiz,jstring filename, jbyteArray image,
                                                               jint width, jint height) {
    LOGE_FMT("");
    char *pfilename = (*env)->GetStringUTFChars(env, filename, 0);
    char *pimage = (*env)->GetByteArrayElements(env,image,JNI_FALSE);


//    unsigned width = 512, height = 512;
//    unsigned char* image = malloc(width * height * 4);
//    unsigned x, y;
//    for(y = 0; y < height; y++)
//        for(x = 0; x < width; x++) {
//            image[4 * width * y + 4 * x + 0] = 255 * !(x & y);
//            image[4 * width * y + 4 * x + 1] = x ^ y;
//            image[4 * width * y + 4 * x + 2] = x | y;
//            image[4 * width * y + 4 * x + 3] = 255;
//        }

    unsigned error = lodepng_encode24_file(pfilename, pimage, width, height);

    (*env)->ReleaseStringUTFChars(env, filename, pfilename);
    (*env)->ReleaseByteArrayElements(env,image,pimage,JNI_FALSE);

    if(error){
        LOGE_FMT("error %u: %s\n", error, lodepng_error_text(error));
        return -1;
    }
    LOGE_FMT("Encode the image succ.");
    return 0;
}


