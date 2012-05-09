#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <jni.h>
#include "JNIFoo.h"

//gcc -shared -fpic -o libfoo.so -I/usr/lib/jvm/jdk1.7.0_03/include/ -I/usr/lib/jvm/jdk1.7.0_03/include/linux/ foo.c

JNIEXPORT jstring JNICALL Java_JNIFoo_nativeFoo (JNIEnv *env, jobject obj)
{
  int i;
  int ds_ret;

  char *newstring;

  jstring ret = 0;

  newstring = (char*)malloc(30);

  if(newstring == NULL)
  {     
      return ret;
  }

  memset(newstring, 0, 30); 
 
  newstring = "foo: Test program of JNI.\n";
  

  ret = (*env)->NewStringUTF(env, newstring);

  //free(newstring);

  return ret;
}
