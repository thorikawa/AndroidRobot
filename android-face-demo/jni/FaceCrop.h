/*
顔を指定サイズで切り抜くためのプログラム
*/

#ifndef FACE_CROP_H_
#define FACE_CROP_H_

#include <opencv2/opencv.hpp>
#include "EigenFace.h"
#include "HaarFaceDetector.h"
#include "log.h"

class FaceCrop {
public:
  FaceCrop(char* basedir);
  ~FaceCrop();
  bool crop (char* inFile);
private:
  HaarFaceDetector* haarFaceDetector;
};

#endif /* FACE_CROP_H_ */