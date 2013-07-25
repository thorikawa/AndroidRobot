#ifndef BLINK_H_
#define BLINK_H_

#include <opencv2/opencv.hpp>
#include <stdio.h>
#include <math.h>
#include "image_pool.h"

#ifdef SIMPLE
#define NUM_F 2
#else
#define NUM_F 3
#define PREV2(x) ((x+NUM_F-2)%NUM_F)
#endif

#define FACES 500

class BlinkDetector {
public:
  BlinkDetector();
  void     findFace (int input_idx, image_pool* pool);
private:
  int      getConnectedComp(IplImage* diff, CvSeq** comp);
  double   findEyePair(IplImage* srcImage, CvSeq* com,int nu, CvRect* e1, CvRect* e2, CvPoint *Fb);
  int      is_eye(CvRect* eye);
  int      is_eye_pair(CvRect* e1, CvRect* e2);
  int      convert_to_eye_region(CvRect* rect, CvRect* eye);
  int      face_region(CvPoint Leye, CvPoint Reye, CvPoint *FaceBox);
  int      crop_rect(IplImage* srcImage, CvPoint *FB,IplImage *RectImg);
  double   FaceScore(IplImage *inp, IplImage *m);
  void     init();
  void     DrawFace(IplImage *F);
  //void     exit_with_message(char* msg);
  CvCapture*  capture;
  IplImage    *frame, *img[NUM_F], *dimg[NUM_F], *cdiff;
  IplImage    *FaceImg[FACES], *mask, *tmpface;
  
  //omit initialization
  uchar        fcount; 
  int          faces;

  CvMemStorage   *storage;
  IplConvKernel  *kernel;
  CvFont          font;
  CvPoint       **FaceBox;
  int NPTS[];

  int      timer;
};

#endif /* BLINK_H_ */