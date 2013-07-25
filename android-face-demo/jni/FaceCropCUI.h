/*
顔を指定サイズで切り抜くためのプログラム
*/

#include <iostream>
#include <fstream>
#include <opencv2/opencv.hpp>
#include "EigenFace.h"
#include "HaarFaceDetector.h"
#include "log.h"

const int DIM_VECTOR = 128;  // 128次元ベクトル

class FaceCrop {
public:
  void crop(char* infile, char* outfile);
}
