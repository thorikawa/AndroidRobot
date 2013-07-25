/*
顔の特徴点のみを抽出するプログラム
*/

#include <iostream>
#include <fstream>
#include <opencv2/opencv.hpp>
#include "HaarFaceDetector.h"
#include "log.h"

const int DIM_VECTOR = 128;  // 128次元ベクトル

/**
 * SURF情報をファイルに出力
 * copy from http://d.hatena.ne.jp/aidiary/20091030/1256905218
 * @param[in]   imageDescriptors    SURF特徴ベクトル情報
 * @return なし
 */
void writeSURF(CvSeq* imageKeypoints, CvSeq* imageDescriptors) {
  for (int i = 0; i < imageKeypoints->total; i++) {
    CvSURFPoint* point = (CvSURFPoint*)cvGetSeqElem(imageKeypoints, i);
    float* descriptor = (float*)cvGetSeqElem(imageDescriptors, i);
    // キーポイント情報（X座標, Y座標, サイズ, ラプラシアン）を書き込む
    // 特徴ベクトルを書き込む
    for (int j = 0; j < DIM_VECTOR; j++) {
      printf("%f\t", descriptor[j]);
    }
    cout << endl;
  }
}

int main (int argc, char** argv) {
  HaarFaceDetector* haarFaceDetector = new HaarFaceDetector(
    "/workspace/OpenCV-2.3.0/data/haarcascades/haarcascade_frontalface_default.xml",
    "/workspace/OpenCV-2.3.0/data/haarcascades/haarcascade_eye_tree_eyeglasses.xml",
    "/workspace/OpenCV-2.3.0/data/haarcascades/haarcascade_mcs_mouth.xml");
    
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
    cvSetImageROI(grayImage, faceRect);

    CvMemStorage* storage = cvCreateMemStorage(0);
    CvSeq* imageKeypoints = 0;
    CvSeq* imageDescriptors = 0;
    CvSURFParams params = cvSURFParams(500, 1);
    
    // 画像からSURFを取得
    cvExtractSURF(grayImage, 0, &imageKeypoints, &imageDescriptors, storage, params);
    LOGE("Image Descriptors: %d", imageDescriptors->total);
    
    for (int i = 0; i < imageKeypoints->total; i++) {
      CvSURFPoint* point = (CvSURFPoint*)cvGetSeqElem(imageKeypoints, i);
      cvCircle(grayImage, cvPoint((int)point->pt.x, (int)point->pt.y), 3, cvScalar(255, 0, 255, 0));
    }
    
    // SURFをファイルに出力
    writeSURF(imageKeypoints, imageDescriptors);
    
    // 後始末
    cvClearSeq(imageKeypoints);
    cvClearSeq(imageDescriptors);
    cvReleaseMemStorage(&storage);    
  }
  
  // 後始末
  cvShowImage("", grayImage);
  cvWaitKey(0);
  
  cvReleaseImage(&grayImage);
  cvDestroyAllWindows();
  
  return 0;
}