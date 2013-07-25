#include <stdio.h>
#include <string>
#include <opencv2/opencv.hpp>
#include <opencv2/legacy/legacy.hpp>
#include "log.h"

#define FACE_WIDTH 96
#define FACE_HEIGHT 96

/**
 * 固有顔のロジックで顔認識を行うクラス
 */
class EigenFace {
public:
  EigenFace(char* trainingDataFile);
  void learn(char* trainFileName);
  int recognize(IplImage* testFace);
  int loadTrainingData();
private:
  char mTrainingDataFile[256];
  IplImage* preProcess(IplImage* testFace);
  void storeTrainingData();
  int  findNearestNeighbor(float * projectedTestFace);
  IplImage ** faceImgArr;
  CvMat    *  personNumTruthMat;
  int nTrainFaces;
  int nEigens;
  IplImage * pAvgTrainImg;
  IplImage ** eigenVectArr;
  CvMat * eigenValMat;
  CvMat * projectedTrainFaceMat;
  CvMat * trainPersonNumMat;
  void doPCA();
  int loadFaceImgArray(char * filename);
};
