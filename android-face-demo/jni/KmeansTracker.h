#ifndef KMEANS_TRACKER_H_
#define KMEANS_TRACKER_H_

#include <opencv2/opencv.hpp>
#include <vector>

#define N_NONTARGET 8
#ifndef PI
#define PI 3.14159265358979
#endif

using namespace std;

struct Feature {
  unsigned char c1;
  unsigned char c2;
  unsigned char c3;
  int x;
  int y;
};

class KmeansTracker {
public:
  KmeansTracker();
  ~KmeansTracker();
  //void setInitial (IplImage* currentFrame, CvBox2D ellipse, vector<CvPoint> targetCenters);
  void setInitial (IplImage* currentFrame, CvRect searchArea, vector<CvPoint> targetCenters);
  void next (IplImage* newFrame);
private:
  IplImage* prevFrame;
  //IplImage* currentFrame;
  //楕円の実装が難しいのでとりあえず矩形で・・・
  //CvBox2D searchArea;
  CvRect searchArea;
  vector<Feature> targetCenters;
  CvRect findBoundingRect(CvPoint2D32f points[], int length);
  void calcNonTarget(vector<Feature> &nonTarget);
};

#endif /* KMEANS_TRACKER_H_ */