/*
 * TestBar.h
 *
 *  Created on: Jul 17, 2010
 *      Author: ethan
 */

#ifndef HAAR_FACE_DETECTOR_H_
#define HAAR_FACE_DETECTOR_H_

#include <opencv2/opencv.hpp>

using namespace std;
using namespace cv;

class HaarFaceDetector{
public:
	HaarFaceDetector(const char* haarcascadeFaceFile, const char* haarcascadeEyeFile, const char* haarcascadeMouthFile);
	bool detectFace(IplImage* srcImage, CvRect* faceRect);
private:
	CvHaarClassifierCascade* cascade_face;
  CvHaarClassifierCascade* cascade_eye;
  CvHaarClassifierCascade* cascade_mouth;
};

#endif /* HAAR_FACE_DETECTOR_H_ */
