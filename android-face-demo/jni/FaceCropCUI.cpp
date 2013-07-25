/*
顔を指定サイズで切り抜くためのプログラム
*/

#include <iostream>
#include <fstream>
#include <opencv2/opencv.hpp>
#include "HaarFaceDetector.h"
#include "log.h"

const int DIM_VECTOR = 128;  // 128次元ベクトル

int main (int argc, char** argv) {
  HaarFaceDetector* haarFaceDetector = new HaarFaceDetector(
    "/workspace/OpenCV-2.3.0/data/haarcascades/haarcascade_frontalface_default.xml",
    "/workspace/OpenCV-2.3.0/data/haarcascades/haarcascade_eye_tree_eyeglasses.xml",
    "/workspace/OpenCV-2.3.0/data/haarcascades/haarcascade_mcs_mouth.xml");

  if (argc != 3) {
    LOGE("please specify input and output file name.");
    return -1;    
  }
  const char* inFile = argc == 3 ? argv[1] : "image/accordion_image_0001.jpg";
  const char* outFile = argc == 3 ? argv[2] : "image/accordion_image_0001.jpg";
  
  // SURF抽出用に画像をグレースケールで読み込む
  IplImage* grayImage = cvLoadImage(inFile, CV_LOAD_IMAGE_GRAYSCALE);
  IplImage* colorImage = cvLoadImage(inFile, CV_LOAD_IMAGE_ANYCOLOR);
  if (!grayImage) {
    LOGE("cannot find image file: %s", inFile);
    return -1;
  }

  CvRect faceRect;
  bool find = haarFaceDetector->detectFace(grayImage, &faceRect);
  if (find) {
    cvSetImageROI(grayImage, faceRect);
    //cvSetImageROI(colorImage, faceRect);
    //IplImage* faceImage = cvCreateImage(cvSize(faceRect.width, faceRect.height), IPL_DEPTH_8U, 3);
    IplImage* resizedFaceImage = cvCreateImage(cvSize(FACE_WIDTH, FACE_HEIGHT), IPL_DEPTH_8U, 1);
    //cvCopy(colorImage, faceImage);
    cvResize(grayImage, resizedFaceImage, CV_INTER_AREA);
    cvSaveImage(outFile, resizedFaceImage);
    
    //cvReleaseImage(&faceImage);
    cvReleaseImage(&resizedFaceImage);
  }
    
  cvReleaseImage(&grayImage);
  cvReleaseImage(&colorImage);
  cvDestroyAllWindows();
  
  return 0;
}