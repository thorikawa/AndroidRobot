#ifndef MY_LOG_H_
#define MY_LOG_H_

#ifdef ANDROID
#include <android/log.h>
#define LOG(...) 
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "PFFaceDetector", __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "PFFaceDetector", __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, "PFFaceDetector", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "PFFaceDetector", __VA_ARGS__)
#else
#define LOG(...) 
#define LOGD(...) fprintf(stderr, __VA_ARGS__); fprintf(stderr, "\n")
#define LOGI(...) fprintf(stderr, __VA_ARGS__); fprintf(stderr, "\n")
#define LOGW(...) fprintf(stderr, __VA_ARGS__); fprintf(stderr, "\n")
#define LOGE(...) fprintf(stderr, __VA_ARGS__); fprintf(stderr, "\n")
#endif

#endif /* MY_LOG_H_ */