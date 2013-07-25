#include "KmeansTracker.h"
#include <cassert>
#include <cmath>
#include "log.h"

#define POINT_TL(r)  cvPoint(r.x, r.y)
#define POINT_BR(r)  cvPoint(r.x + r.width, r.y + r.height)
#define CENTER(r) cvPoint(r.x + (r.width/2), r.y + (r.height/2))
#define POINTS(r)  POINT_TL(r), POINT_BR(r)

#define DUMP(tag, f) LOG("[%s]feature = (%d %d %d %d %d)", tag, f.c1, f.c2, f.c3, f.x, f.y);

/**
 * 5次元特徴空間におけるユークリッド距離の二乗を求める
 */
inline float squareDistance (const Feature *one, const Feature *another) {
  float r =
    pow(one->c1 - another->c1, 2.0)
  + pow(one->c2 - another->c2, 2.0)
  + pow(one->c3 - another->c3, 2.0)
  + pow(one->x  - another->x , 2.0)
  + pow(one->y  - another->y , 2.0);
  return r;
}

/**
 * トラッキング情報の初期化
 */
//void KmeansTracker::setInitial (IplImage* currentFrame, CvBox2D ellipse, vector<CvPoint> targetCenterPoints) {
void KmeansTracker::setInitial (IplImage* currentFrame, CvRect searchArea, vector<CvPoint> targetCenterPoints) {
  prevFrame = cvCloneImage(currentFrame);
  this->searchArea = searchArea;
  
  // ターゲット点から特徴空間上の座標を求めておく
  LOG("calc target centers in features space");
  targetCenters.clear();
  int tn = targetCenterPoints.size();
  for (int i=0; i<tn; i++) {
    CvPoint pt = targetCenterPoints[i];
    int index = pt.y * currentFrame->widthStep + pt.x;
    Feature f = {
      currentFrame->imageData[index],
      currentFrame->imageData[index+1],
      currentFrame->imageData[index+2],
      pt.x,
      pt.y
    };
    targetCenters.push_back(f);
  }

  return;
}

/**
 * コンストラクタ
 */
KmeansTracker::KmeansTracker() : prevFrame(NULL) {}

/**
 * デストラクタ
 */
KmeansTracker::~KmeansTracker() {
  if (prevFrame != NULL) {
    cvReleaseImage(&prevFrame);
  }
}

/**
 * 引数で与えられた点集合を内包する最小の矩形を見つける
 */
CvRect KmeansTracker::findBoundingRect(CvPoint2D32f points[], int length) {
  LOG("cvBoundingRect");
  CvMemStorage *storage = cvCreateMemStorage(0);
  CvSeq* pointSeq = cvCreateSeq(CV_SEQ_ELTYPE_POINT , sizeof (CvSeq), sizeof (CvPoint), storage);
  LOG("push start");
  for (int i=0; i<length; i++) {
    CvPoint pt = cvPoint(points[i].x, points[i].y);
    LOG("push (%d,%d)", pt.x, pt.y);
    cvSeqPush(pointSeq, &pt);
  }
  LOG("push end");
  CvRect boundingRect = cvBoundingRect(pointSeq);
  cvReleaseMemStorage(&storage);
  return boundingRect;
}

/**
 * 現在の探索領域からN_NONTARGET個の非ターゲットのクラスタ中心点を求める
 */
void KmeansTracker::calcNonTarget(vector<Feature> &nonTarget) {
  int allRound = 2*(searchArea.width+searchArea.height);
  int interval = allRound / N_NONTARGET;
  //LOG("interval = %d", interval);
  int x = 0;
  int y = 0;
  int phase = 0;
  for(int i=0; i<N_NONTARGET; i++) {
    CvPoint pt = cvPoint(searchArea.x + x, searchArea.y + y);
    //LOG("get(%d,%d)", x, y);
    int index = pt.y * prevFrame->widthStep + pt.x;
    Feature f = {
      prevFrame->imageData[index],
      prevFrame->imageData[index+1],
      prevFrame->imageData[index+2],
      pt.x,
      pt.y
    };
    nonTarget.push_back(f);
    switch(phase) {
      case 0:
      x+=interval;
      if (x >= searchArea.width) {
        interval = x-searchArea.width+1;
        x = searchArea.width-1;
      } else {
        break;
      }
      case 1:
      y+=interval;
      if (y >= searchArea.height) {
        interval = y-searchArea.height+1;
        y = searchArea.height-1;
      } else {
        break;
      }
      case 2:
      x-=interval;
      if (x < 0) {
        interval = -x;
        x = 0;
      } else {
        break;
      }
      case 3:
      y-=interval;
      assert(y>=0);
      break;
    }
  }
  /*
  float dDegree = PI / N_NONTARGET;
  float degree = 0;
  float vCos = cos(degree);
  float vSin = sin(degree);
  for(int i=0; i<N_NONTARGET; i++) {
    float a = (pow(vCos, (float)2.0) / pow((float)(searchArea.size.width/2.0), (float)2.0))
     + (pow(vSin, (float)2.0) / pow ((float)(searchArea.size.height/2.0), (float)2.0));
    float r = sqrt(1.0 / a);
    // 回転後の座標系におけるx,y
    float x = r * vCos;
    float y = r * vSin;
    // 現在の座標系におけるx,y
    float p = x*ellipseCos - y*ellipseSin;
    float q = x*ellipseSin + y*ellipseCos;
    int index = q*prevFrame->widthStep + p;
    Feature f = {
      prevFrame->imageData[index],
      prevFrame->imageData[index+1],
      prevFrame->imageData[index+2],
      p,
      q
    };
    nonTargets.push_back(f);
    degree += dDegree;
  }
  */
  //LOG("calc non target end");
}

/**
 * トラッキング開始後の各フレームごとの処理
 */
void KmeansTracker::next (IplImage* newFrame) {
  assert(prevFrame != NULL);
  assert(newFrame->nChannels == 3);

  // よく使う値を予め求めておく
  //float ellipseDegree = PI*(searchArea.angle /180.0);
  //float ellipseCos = cos(ellipseDegree);
  //float ellipseSin = sin(ellipseDegree);
  
  
  // TODO: check detection failure and recovery if possible
  
  // k-means cluster pixcels within ellipse
  int tn = targetCenters.size();
  if (tn <= 0) {
    LOGE("We lost all target centers...");
    return;
  }
  for (int i=0; i<tn; i++) {
    // ターゲット点を描画
    Feature f = targetCenters[i];
    cvCircle(newFrame, cvPoint(f.x, f.y), 3, CV_RGB(60,255,50));
  }

  // 非ターゲット点を求めておく
  LOG("calc non target centers in features space");
  vector<Feature> nonTargets;
  calcNonTarget(nonTargets);

  
  vector< vector<CvPoint> > targetPointsArr(tn);
    
  CvPoint2D32f pt[4];
  LOG("cvBoxPoints");
  // cvBoxPoints(searchArea, pt);
  // 外接矩形を見つける
  //CvRect boundingRect = findBoundingRect(pt, 4);
  CvRect boundingRect = searchArea;
  
  /* for debug */
  LOG("boundingRect=(%d,%d,%d,%d)", boundingRect.x, boundingRect.y, boundingRect.width, boundingRect.height);
  
  // ROIで探索領域を狭める
  LOG("Start K-means Clustering");
  for (int y=boundingRect.y; y<boundingRect.y+boundingRect.height; y+=4) {
    for (int x=boundingRect.x; x<boundingRect.x+boundingRect.width; x+=3) {
      int index = y*newFrame->widthStep + x*3;
      /*
       * x,y,c1,c2,c3の五次元空間上でk-meansを行う
       */

      Feature f = {
        newFrame->imageData[index],
        newFrame->imageData[index+1],
        newFrame->imageData[index+2],
        x,
        y
      };
      
      // ターゲット点と比較
      float dt = 99999999;
      int dtIndex = -1;
      for (int i=0; i<tn; i++) {
        Feature tf = targetCenters[i];
        //DUMP("TARGET", tf);
        float d = squareDistance(&f, &tf);
        if (d < dt) {
          dtIndex = i;
          dt = d;
        }
      }
      
      // 非ターゲット点と比較
      // TODO: 中心と当該点とを結ぶ線分と楕円との交点を非ターゲット点とする
      float dn = 99999999;
      int dnIndex = -1;
      for (int i=0; i<N_NONTARGET; i++) {
        Feature ntf = nonTargets[i];
        //DUMP("NONT", ntf);
        float d = squareDistance(&f, &ntf);
        if (d < dn) {
          dnIndex = i;
          dn = d;
        }
      }

      if (dt < dn) {
        // ターゲット点
        // LOG("this is target point(%d,%d) dt=%f, dn=%f", x, y, dt, dn);
        CvPoint pt = cvPoint(x, y);
        targetPointsArr[dtIndex].push_back(pt);
      }
    }
  }
  
  // update search area (ellipse)
  // update targetCenters
  LOG("update target centers");
  CvMemStorage* storage = cvCreateMemStorage(0);
  CvSeq* pointSeq = cvCreateSeq(CV_SEQ_ELTYPE_POINT, sizeof(CvSeq), sizeof(CvPoint), storage);
   
  targetCenters.clear();
  vector<Feature> targets;
  float allSumX = 0;
  float allSumY = 0;
  int allCount = 0;
  for (int i=0; i<tn; i++) {
    float sumX=0;
    float sumY=0;
    int targetPointsNum = targetPointsArr[i].size();
    allCount += targetPointsNum;
    //LOG("parse %d th target gruop num=%d", i, targetPointsNum);
    for (int j=0; j<targetPointsNum; j++) {
      CvPoint t = targetPointsArr[i][j];
      //LOG("check point (%d,%d)", t.x, t.y);
      sumX += t.x;
      sumY += t.y;
      // cvSeqに挿入
      cvSeqPush(pointSeq, &t);
    }
    allSumX += sumX;
    allSumY += sumY;
    
    if (targetPointsNum > 0) {
      int x = sumX/targetPointsNum;
      int y = sumY/targetPointsNum;
      LOG("new center (%d,%d) sumX=%f, sumY=%f, targetPointsNum=%d", x, y, sumX, sumY, targetPointsNum);
      int index = y*newFrame->widthStep + x*3;
      Feature f = {
        newFrame->imageData[index],
        newFrame->imageData[index+1],
        newFrame->imageData[index+2],
        x,
        y
      };
      // TODO ここで新しいtargetCenterと旧targetCenterの色を比較する？
      targetCenters.push_back(f);
    } else {
      // ターゲットに属する点が一つも存在しない場合
      LOG("There is no point in %d th target...", i);
    }
  }
  
  //LOG("find new ellipse");
  // new ellipse
  searchArea = cvBoundingRect(pointSeq);
  int dx = searchArea.width  * 0.01;
  int dy = searchArea.height * 0.01;
  searchArea.x -= dx;
  searchArea.y -= dy;
  searchArea.width  += dx*2;
  searchArea.height += dy*2;
  //validate
  if (searchArea.x<0) searchArea.x = 0;
  if (searchArea.y<0) searchArea.y = 0;
  if (searchArea.width + searchArea.x > newFrame->width) searchArea.width = newFrame->width - searchArea.x - 1;
  if (searchArea.height + searchArea.y > newFrame->height) searchArea.height = newFrame->height - searchArea.y - 1;
  
  cvReleaseMemStorage(&storage);
  //searchArea = cvFitEllipse2(pointSeq);
  //LOG("new Ellipse=(%d,%d,%d,%d)", searchArea.center.x, searchArea.center.y, searchArea.size.width, searchArea.size.height);
  LOG("new SearchArea=(%d,%d,%d,%d)", searchArea.x, searchArea.y, searchArea.width, searchArea.height);
  
  // 楕円を描画
  //cvEllipseBox(newFrame, searchArea, CV_RGB(255,50,50));
  cvRectangle(newFrame, POINTS(searchArea), CV_RGB(255,50,50));
  
  // 物体が移動していることを考慮して拡大
  //searchArea.size.width = searchArea.size.width * 1.25;
  //searchArea.size.height = searchArea.size.height * 1.25;

  // 解放処理
  cvReleaseMemStorage(&storage);
  
  // 前フレームの更新
  cvReleaseImage(&prevFrame);
  prevFrame = cvCloneImage(newFrame);
  
  return;
}