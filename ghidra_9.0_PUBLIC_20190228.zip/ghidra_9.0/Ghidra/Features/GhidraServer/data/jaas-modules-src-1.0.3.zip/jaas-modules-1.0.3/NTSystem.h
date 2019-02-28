/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_tagish_auth_win32_NTSystem */

#ifndef _Included_com_tagish_auth_win32_NTSystem
#define _Included_com_tagish_auth_win32_NTSystem
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_tagish_auth_win32_NTSystem
 * Method:    getVersion
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_tagish_auth_win32_NTSystem_getVersion
  (JNIEnv *, jobject);

/*
 * Class:     com_tagish_auth_win32_NTSystem
 * Method:    finalize
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_tagish_auth_win32_NTSystem_finalize
  (JNIEnv *, jobject);

/*
 * Class:     com_tagish_auth_win32_NTSystem
 * Method:    getDomain
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_tagish_auth_win32_NTSystem_getDomain
  (JNIEnv *, jobject);

/*
 * Class:     com_tagish_auth_win32_NTSystem
 * Method:    getDomainSID
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_tagish_auth_win32_NTSystem_getDomainSID
  (JNIEnv *, jobject);

/*
 * Class:     com_tagish_auth_win32_NTSystem
 * Method:    getGroupIDs
 * Signature: ()[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_com_tagish_auth_win32_NTSystem_getGroupIDs
  (JNIEnv *, jobject);

/*
 * Class:     com_tagish_auth_win32_NTSystem
 * Method:    getGroupNames
 * Signature: (Z)[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_com_tagish_auth_win32_NTSystem_getGroupNames
  (JNIEnv *, jobject, jboolean);

/*
 * Class:     com_tagish_auth_win32_NTSystem
 * Method:    getImpersonationToken
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_tagish_auth_win32_NTSystem_getImpersonationToken
  (JNIEnv *, jobject);

/*
 * Class:     com_tagish_auth_win32_NTSystem
 * Method:    getName
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_tagish_auth_win32_NTSystem_getName
  (JNIEnv *, jobject);

/*
 * Class:     com_tagish_auth_win32_NTSystem
 * Method:    getPrimaryGroupID
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_tagish_auth_win32_NTSystem_getPrimaryGroupID
  (JNIEnv *, jobject);

/*
 * Class:     com_tagish_auth_win32_NTSystem
 * Method:    getPrimaryGroupName
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_tagish_auth_win32_NTSystem_getPrimaryGroupName
  (JNIEnv *, jobject);

/*
 * Class:     com_tagish_auth_win32_NTSystem
 * Method:    getUserSID
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_tagish_auth_win32_NTSystem_getUserSID
  (JNIEnv *, jobject);

/*
 * Class:     com_tagish_auth_win32_NTSystem
 * Method:    logon
 * Signature: (Ljava/lang/String;[CLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_tagish_auth_win32_NTSystem_logon
  (JNIEnv *, jobject, jstring, jcharArray, jstring);

/*
 * Class:     com_tagish_auth_win32_NTSystem
 * Method:    logoff
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_tagish_auth_win32_NTSystem_logoff
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif