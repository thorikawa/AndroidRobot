/*
顔の特徴点のみを抽出するプログラム
*/

#include <iostream>
#include <fstream>
#include <opencv2/opencv.hpp>
#include "HaarFaceDetector.h"
#include "ObjectMatcher.h"
#include "log.h"

#define POINT_TL(r)  cvPoint(r.x, r.y)
#define POINT_BR(r)  cvPoint(r.x + r.width, r.y + r.height)
#define POINTS(r)  POINT_TL(r), POINT_BR(r)

const int DIM_VECTOR = 128;  // 128次元ベクトル

int main (int argc, char** argv) {
  
  HaarFaceDetector* haarFaceDetector = new HaarFaceDetector(
    "/workspace/OpenCV-2.3.0/data/haarcascades/haarcascade_frontalface_default.xml",
    "/workspace/OpenCV-2.3.0/data/haarcascades/haarcascade_eye_tree_eyeglasses.xml",
    "/workspace/OpenCV-2.3.0/data/haarcascades/haarcascade_mcs_mouth.xml");
  ObjectMatcher* objectMatcher = new ObjectMatcher();
  // load feature vector
  objectMatcher->loadDescription("/workspace/OpenCV-2.3.0/android/apps/FaceRecognition/assets/features/takahiro.txt");
  objectMatcher->loadDescription("/workspace/OpenCV-2.3.0/android/apps/FaceRecognition/assets/features/akita.txt");
  objectMatcher->loadDescription("/workspace/OpenCV-2.3.0/android/apps/FaceRecognition/assets/features/kei.txt");
  objectMatcher->loadDescription("/workspace/OpenCV-2.3.0/android/apps/FaceRecognition/assets/features/koga.txt");
  objectMatcher->loadDescription("/workspace/OpenCV-2.3.0/android/apps/FaceRecognition/assets/features/sato.txt");
  objectMatcher->loadDescription("/workspace/OpenCV-2.3.0/android/apps/FaceRecognition/assets/features/pan.txt");
  //cvInitFont(&font, CV_FONT_HERSHEY_DUPLEX, 1.0, 1.0, 0, 3, 8);

  const char* imageFile = argc == 2 ? argv[1] : "image/accordion_image_0001.jpg";
  
  // SURF抽出用に画像をグレースケールで読み込む
  IplImage* grayImage = cvLoadImage(imageFile, CV_LOAD_IMAGE_GRAYSCALE);
  if (!grayImage) {
    LOGE("cannot find image file: %s", imageFile);
    return -1;
  }

  CvRect faceRect;
  bool find = haarFaceDetector->detectFace(grayImage, &faceRect);
  if (find) {
    cvRectangle(grayImage, POINTS(faceRect), CV_RGB(255,255,0), 2, 8, 0);
    cvSetImageROI(grayImage, faceRect);

    int objId = objectMatcher->match(grayImage);
  }

  cvShowImage("", grayImage);
  cvWaitKey(0);
  
  // 後始末
  cvReleaseImage(&grayImage);
  cvDestroyAllWindows();
  
  return 0;
}