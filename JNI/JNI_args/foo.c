#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <jni.h>
#include "JNIFoo.h"

//gcc -shared -fpic -o libfoo.so -I/usr/lib/jvm/jdk1.7.0_03/include/ -I/usr/lib/jvm/jdk1.7.0_03/include/linux/ foo.c

JNIEXPORT void JNICALL Java_JNIFoo_nativeFoo (JNIEnv *env, jobject obj,jcharArray jchar_buffer,jint length,jstring jstr)
{
  int i;
  int ds_ret;
  jchar * array;
  array = (*env)->GetCharArrayElements(env,jchar_buffer,0);
  for(i = 0; i < length; i++) {
    printf("%c ",(char)(array[i]));
  }
  printf("\n");
  (*env)->ReleaseCharArrayElements(env,jchar_buffer,array, 0);

  const char *nativeString = (*env)->GetStringUTFChars(env, jstr, 0);
  for(i = 0; i < strlen(nativeString); i++) {
    printf("%c ",(char)(nativeString[i]));
  }
  (*env)->ReleaseStringUTFChars(env, jstr, nativeString);
  jstring ret_str = 0;
  ret_str = (*env)->NewStringUTF(env, array);
  return ret_str;
/*
  char newstring[50];
  jstring ret = 0;
  memset(newstring, 0, 50); 
  printf("num:%s\n",array);

  if(num < 5) {
	strcpy(newstring,"lessThanFive");
  } else {
	strcpy(newstring,"largerOrEqualThanFive");
  }
  ret = (*env)->NewStringUTF(env, newstring);

  //free(newstring);

  return ret;
*/
}
