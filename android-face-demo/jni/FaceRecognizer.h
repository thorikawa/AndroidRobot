/*
 * TestBar.h
 *
 *  Created on: Jul 17, 2010
 *      Author: ethan
 */

#ifndef FACE_RECOGNIZER_H_
#define FACE_RECOGNIZER_H_

#include <opencv2/opencv.hpp>

#include "image_pool.h"
#include "HaarFaceDetector.h"
#include "ObjectMatcher.h"
#include "EigenFace.h"

using namespace std;
using namespace cv;

class FaceRecognizer{
public:
	FaceRecognizer();
  ~FaceRecognizer();
	int recognize(int idx, image_pool* pool);
  void learn(char* trainFileName);
private:
  HaarFaceDetector* haarFaceDetector;
  ObjectMatcher* objectMatcher;
  EigenFace* eigenFace;
  CvFont font;
};

#endif /* FACE_RECOGNIZER_H_ */
