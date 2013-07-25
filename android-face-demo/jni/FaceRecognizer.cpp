#include "FaceRecognizer.h"
#include "log.h"

#define POINT_TL(r)  cvPoint(r.x, r.y)
#define RT_POINT_TL(r)  cvPoint(r.y, r.x)
#define POINT_BR(r)  cvPoint(r.x + r.width, r.y + r.height)
#define RT_POINT_BR(r)  cvPoint(r.y + r.height, r.x + r.width)
#define POINTS(r)  POINT_TL(r), POINT_BR(r)
#define RT_POINTS(r)  RT_POINT_TL(r), RT_POINT_BR(r)

/**
 * コンストラクタ
 */
FaceRecognizer::FaceRecognizer () {
  haarFaceDetector = new HaarFaceDetector(
    "/data/data/com.polysfactory.facerecognition/files/haarcascade_frontalface_default.xml",
    "/data/data/com.polysfactory.facerecognition/files/haarcascade_eye_tree_eyeglasses.xml",
    "/data/data/com.polysfactory.facerecognition/files/haarcascade_mcs_mouth.xml");  
  //eigen face recognizer
  eigenFace = new EigenFace("/data/data/com.polysfactory.facerecognition/files/facedata.xml");
  eigenFace->loadTrainingData();
  
  cvInitFont(&font, CV_FONT_HERSHEY_DUPLEX, 1.0, 1.0, 0, 3, 8);
}

/**
 * デコンストラクタ
 */
FaceRecognizer::~FaceRecognizer () {
  delete(haarFaceDetector);
}

/**
 * 指定された画像インデックスファイルから顔画像を認識しなおす
 */
void FaceRecognizer::learn(char* trainFileName){
  eigenFace->learn(trainFileName);
  eigenFace->loadTrainingData();
  LOGD("learn end");
}

/**
 * 顔検出&顔認識
 */
int FaceRecognizer::recognize(int input_idx, image_pool* pool) {
  double scale = 3.0;
  double t = 0;
  LOGD("recognizeFace Start");
  
  IplImage srcImage = pool->getImage(input_idx);
  IplImage greyImage = pool->getGrey(input_idx);
  CvSize size = cvGetSize(&greyImage);
  CvSize rotatedSize = cvSize(size.height, size.width);
  IplImage* rotatedImage = cvCreateImage(rotatedSize, IPL_DEPTH_8U, 1);
  // 90度回転
  cvTranspose(&greyImage, rotatedImage);

  if (&greyImage == NULL) {
    LOGE("cannot get an image frame!\n");
    return -1;
  }
  
  CvRect faceRect;
  bool found = haarFaceDetector->detectFace(rotatedImage, &faceRect);
  if (found) {
    cvRectangle(&srcImage, RT_POINTS(faceRect), CV_RGB(255,255,0), 2, 8, 0);
    cvSetImageROI(rotatedImage, faceRect);
    int objId = eigenFace->recognize(rotatedImage);
    LOGD("objectId=%d", objId);
    char txt[256];
    sprintf(txt, "id=%d",objId);
    //cvPutText(&srcImage, txt, POINT_TL(faceRect), &font, CV_RGB(0,255,255));
    cvPutText(&srcImage, txt, RT_POINT_TL(faceRect), &font, CV_RGB(0,255,255));
    return objId;
  } else {
    // 見つからない
    LOGW("can't find faces");
    return -1;
  }
}
