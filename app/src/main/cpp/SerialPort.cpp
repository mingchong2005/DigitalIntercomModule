/*
 * Copyright 2009 Cedric Priscal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <jni.h>
#include <string>
#include <assert.h>
#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#include <stdio.h>
#include <stdlib.h>
#include <errno.h>

#include <android/log.h>

#include "SerialPort.h"

static const char *TAG="serial_port";
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)


static const char *gClassName = "com/example/digitalintercommodule/utils/SerialPort";

#ifndef NELEM
# define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))
#endif

static speed_t getBaudrate(jint baudrate)
{
	switch(baudrate) {
	case 0: return B0;
	case 50: return B50;
	case 75: return B75;
	case 110: return B110;
	case 134: return B134;
	case 150: return B150;
	case 200: return B200;
	case 300: return B300;
	case 600: return B600;
	case 1200: return B1200;
	case 1800: return B1800;
	case 2400: return B2400;
	case 4800: return B4800;
	case 9600: return B9600;
	case 19200: return B19200;
	case 38400: return B38400;
	case 57600: return B57600;
	case 115200: return B115200;
	case 230400: return B230400;
	case 460800: return B460800;
	case 500000: return B500000;
	case 576000: return B576000;
	case 921600: return B921600;
	case 1000000: return B1000000;
	case 1152000: return B1152000;
	case 1500000: return B1500000;
	case 2000000: return B2000000;
	case 2500000: return B2500000;
	case 3000000: return B3000000;
	case 3500000: return B3500000;
	case 4000000: return B4000000;
	default: return -1;
	}
}

/*
 * Class:     cedric_serial_SerialPort
 * Method:    open
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT jobject JNICALL SerialPort_Open(JNIEnv *env, jobject thiz, jstring path, jint baudrate)
{
	int fd;
	speed_t speed;
	jobject mFileDescriptor;

	/* Check arguments */
	{
		speed = getBaudrate(baudrate);
		if (speed == -1) {
			/* TODO: throw an exception */
			LOGE("Invalid baudrate");
			return NULL;
		}
	}

	/* Opening device */
	{
		jboolean iscopy;
		const char *path_utf = env->GetStringUTFChars(path, &iscopy);
		LOGD("Opening serial port %s", path_utf);
		//fd = open(path_utf, O_RDWR | O_DIRECT | O_SYNC);
		fd = open(path_utf, O_RDWR | O_SYNC);

		LOGD("serial_port open() fd = %d", fd);
		env->ReleaseStringUTFChars(path, path_utf);
		if (fd == -1)
		{
			/* Throw an exception */
			LOGE("Cannot open port");
			/* TODO: throw an exception */
			return NULL;
		}
	}

	/* Configure device */
	{
		struct termios cfg;
		LOGD("Configuring serial port");
		if (tcgetattr(fd, &cfg))
		{
			LOGE("tcgetattr() failed");
			close(fd);
			/* TODO: throw an exception */
			return NULL;
		}

		cfmakeraw(&cfg);
		cfsetispeed(&cfg, speed);
		cfsetospeed(&cfg, speed);

		cfg.c_cflag &=~CSIZE;
        cfg.c_cflag |=CS8;
	    cfg.c_cflag &=~PARENB;

		cfg.c_cflag &= ~CSTOPB;

		tcflush(fd,TCIFLUSH);
		
		if (tcsetattr(fd, TCSANOW, &cfg))
		{
			LOGE("tcsetattr() failed");
			close(fd);
			/* TODO: throw an exception */
			return NULL;
		}
	}

	/* Create a corresponding file descriptor */
	{
		jclass cFileDescriptor = env->FindClass("java/io/FileDescriptor");
		jmethodID iFileDescriptor = env->GetMethodID(cFileDescriptor, "<init>", "()V");
		jfieldID descriptorID = env->GetFieldID(cFileDescriptor, "descriptor", "I");
		mFileDescriptor = env->NewObject(cFileDescriptor, iFileDescriptor);
		env->SetIntField(mFileDescriptor, descriptorID, (jint)fd);
	}

	return mFileDescriptor;
}

/*
 * Class:     cedric_serial_SerialPort
 * Method:    close
 * Signature: ()V
 */
JNIEXPORT jint JNICALL SerialPort_Close(JNIEnv * env, jobject thiz)
{
	jclass SerialPortClass = env->GetObjectClass(thiz);
	jclass FileDescriptorClass = env->FindClass( "java/io/FileDescriptor");

	jfieldID mFdID = env->GetFieldID(SerialPortClass, "mFd", "Ljava/io/FileDescriptor;");
	jfieldID descriptorID = env->GetFieldID(FileDescriptorClass, "descriptor", "I");

	jobject mFd = env->GetObjectField(thiz, mFdID);
	jint descriptor = env->GetIntField(mFd, descriptorID);

	LOGD("serial_port close(fd = %d)", descriptor);
	close(descriptor);
    return 1;
}

static const char *Dev_Name =  "/dev/gpio_custom";
JNIEXPORT jint JNICALL Gpiopower(JNIEnv * env, jobject thiz, jint isOn)
{
	int ret = 0;
	int pinVal = 0;
	int handle;

	LOGD("serial_port Gpiopower ,pinVal,%d",pinVal);

	handle = open(Dev_Name, O_RDWR);
	if (handle < 0){
		return 	ret;
	}

	ret = ioctl(handle, (isOn == 1) ? GPIO_CUSTOM_IOCTL_POWER_ON : GPIO_CUSTOM_IOCTL_POWER_OFF, (long)&pinVal);
	if (ret < 0) {
		LOGD("Gpiopower ,result,%d",ret);
	}

	close(handle);

	return pinVal;
}

JNIEXPORT jint JNICALL Gpiospkmic(JNIEnv * env, jobject thiz, jint isOn)
{
	int ret = 0;
	int pinVal = 0;
	int handle;

	LOGD("serial_port Gpiopower ,pinVal,%d",pinVal);

	handle = open(Dev_Name, O_RDWR);
	if (handle < 0){
		return 	ret;
	}

	ret = ioctl(handle, (isOn == 1) ? GPIO_CUSTOM_IOCTL_SPK_MIC_CTRL_ON : GPIO_CUSTOM_IOCTL_SPK_MIC_CTRL_OFF, (long)&pinVal);
	if (ret < 0) {
		LOGD("Gpiopower ,result,%d",ret);
	}

	close(handle);

	return pinVal;
}

JNIEXPORT jint JNICALL SpeakerOn(JNIEnv * env, jobject thiz, jint isOn)
{
	int ret = 0;
	int pinVal = 0;
	int handle;

	LOGD("serial_port Gpiopower ,pinVal,%d",pinVal);

	handle = open(Dev_Name, O_RDWR);
	if (handle < 0){
		return 	ret;
	}

	ret = ioctl(handle, (isOn == 1) ? GPIO_CUSTOM_IOCTL_SPEAKER_ON : GPIO_CUSTOM_IOCTL_SPEAKER_OFF, (long)&pinVal);
	if (ret < 0) {
		LOGD("Gpiopower ,result,%d",ret);
	}

	close(handle);

	return pinVal;
}


static JNINativeMethod gMethods[] = {
        { "nativeSerialPortOpen", "(Ljava/lang/String;I)Ljava/io/FileDescriptor;",(void*) SerialPort_Open },
        { "nativeSerialPortClose", "()I",(void*) SerialPort_Close },
		{ "nativeGpiopower", "(I)I",(void*) Gpiopower },
		{ "nativeGpiospkmic", "(I)I",(void*) Gpiospkmic },
		{ "nativeSpeakerOn", "(I)I",(void*) SpeakerOn },
};


static jint registerNativeMethods(JNIEnv* env, const char* className, JNINativeMethod* methods, int numMethods) {

jclass clazz;

clazz = env->FindClass(className);

if(env->ExceptionCheck()){
env->ExceptionDescribe();
env->ExceptionClear();
}

if (clazz == NULL) {
LOGE("Native registration unable to find class '%s'", className);
return JNI_FALSE;
}

if (env->RegisterNatives(clazz, methods, numMethods) < 0) {
LOGE("RegisterNatives failed for '%s'", className);
return JNI_FALSE;
}

return JNI_TRUE;
}

static jint registerNatives(JNIEnv* env){

	jint ret = JNI_FALSE;

	if (registerNativeMethods(env, gClassName, gMethods, NELEM(gMethods))){
		ret = JNI_TRUE;
	}

	return ret;
}

void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved) {
}

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
	JNIEnv* env = NULL;
	jint result = -1;

	if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
		LOGE("ERROR: GetEnv failed\n");
		goto bail;
	}

	assert(env != NULL);

	if (registerNatives(env) != JNI_TRUE) {
		LOGE("ERROR: registerNatives failed");
		goto bail;
	}

	result = JNI_VERSION_1_4;
	bail:
	return result;
}
