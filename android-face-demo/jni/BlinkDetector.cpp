#include "BlinkDetector.h"
#include <android/log.h>

#define SIMPLE 1
  
#define IMAGE_W                320
#define IMAGE_H               240

#define DETECTION_MODE      1
#define TRACKING_MODE       2

#define SIZX 64
#define SIZY 80

#define POINT_TL(r)  cvPoint(r.x, r.y)
#define POINT_BR(r)  cvPoint(r.x + r.width, r.y + r.height)
#define POINTS(r)  POINT_TL(r), POINT_BR(r)
#define CENTER(r) cvPoint(r.x + (r.width/2), r.y + (r.height/2))

#define max(x,y) (((x)>(y))?(x):(y))
#define min(x,y) (((x)<(y))?(x):(y))
#define dist(s,d) sqrt((s->x-d->x)*(s->x-d->x)+(s->y-d->y)*(s->y-d->y))

#define NEXT(x) ((x+1)%NUM_F)
#define PREV(x) ((x+NUM_F-1)%NUM_F)

#define  TIME_LIMIT 1
#define  SCTH 0.12

/*
#define LOG(...) __android_log_print(ANDROID_LOG_DEBUG, "PFFaceDetector", __VA_ARGS__)
*/
#define LOG(...) 
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "PFFaceDetector", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "PFFaceDetector", __VA_ARGS__)

/**
 * Constructor
 */
BlinkDetector::BlinkDetector() {
  LOG("Initialize BlinkDetector\n");

  // Initialize Font
  cvInitFont(&font, CV_FONT_HERSHEY_DUPLEX, 1.0, 1.0, 0, 3, 8);

  // Storage for Temporally Different Image Regions
  storage = cvCreateMemStorage(0);
  if (!storage) {
    LOG("cannot allocate memory storage!\n");
    return;
  }

  //Structuring Element for Morphological Operation
#ifdef SIMPLE
  kernel = cvCreateStructuringElementEx(3,3,1,1,CV_SHAPE_CROSS, NULL);
#else
  kernel = cvCreateStructuringElementEx(3,3,1,1,CV_SHAPE_RECT, NULL);
#endif
  if (!kernel) {
    LOGE("Memory Allocation Error.\n");
    return;
  }
  
  for (int i=0; i<NUM_F;i++) {
    img[i] = cvCreateImage(cvSize(IMAGE_W, IMAGE_H), 8, 1);
    dimg[i] = cvCreateImage(cvSize(IMAGE_W, IMAGE_H), 8, 1);
    if (!img[i] || !dimg[i]) {
      LOGE("Memory Allocation Error.\n");
    }
  }

  cdiff= cvCreateImage(cvSize(IMAGE_W, IMAGE_H), 8, 1);
  if (!cdiff) {
    LOGE("Memory Allocation Error.\n");
  }

  for (int i=0; i<FACES;i++) {
    FaceImg[i]=cvCreateImage(cvSize(SIZX,SIZY), 8, 1);
  }
  //Face Bounding Box
  FaceBox=(CvPoint **) cvAlloc (sizeof (CvPoint *) * 1);
  FaceBox[0]= (CvPoint *) cvAlloc (sizeof (CvPoint) * 4);

  tmpface=cvCreateImage( cvGetSize(FaceImg[0]), 8, 1 );
  mask=cvCreateImage( cvGetSize(FaceImg[0]), 8, 1 );
/***************** Draw Face Mask **************************/
  cvSet(mask,cvScalarAll(128),NULL);
  DrawFace(mask);

/**********************************************************************/

}

void BlinkDetector::findFace (int input_idx, image_pool* pool) {
  int nc;
  CvSeq*   comp = 0;
  CvRect   eye1, eye2;
  int      stage = DETECTION_MODE;
  double   score;
  char     txt[80];
  CvSize   _size;
  faces = 0;

  double t = cvGetTickCount() / (cvGetTickFrequency() * 1000.0);
  char outfile[256];
  sprintf(outfile, "/sdcard/tmp/out%f.jpg", t);
  LOGI("**%f**\n", t);

  IplImage srcImage = pool->getImage(input_idx);
  if (&srcImage == NULL) {
    LOGE("cannot get an image frame!\n");
    return;
  }
  //cvSmooth(&srcImage, &srcImage);
  CvSize size = cvGetSize(&srcImage);
  //LOG("size=(%d,%d)\n", size.width, size.height);
  
  //IplImage grayImage = pool->getGrey(input_idx);
  //img[fcount] = cvCloneImage(&grayImage);
  //cvCopy(&grayImage, img[fcount]);
  cvCvtColor(&srcImage, img[fcount], CV_RGB2GRAY);

  //Frame by frame subtraction & binarization
  cvSub(img[fcount], img[PREV(fcount)], dimg[fcount], NULL);
  cvThreshold(dimg[fcount], dimg[fcount], 5, 255, CV_THRESH_BINARY);
#ifdef SIMPLE
#define EYE_RATIO 1.
  cvCopy(dimg[fcount],cdiff,NULL);
  cvMorphologyEx(cdiff,cdiff,NULL,kernel,CV_MOP_OPEN,1);
#else
#define EYE_RATIO 1.3
  cvAnd(dimg[fcount], dimg[PREV(fcount)], cdiff, NULL);
  cvMorphologyEx(cdiff,cdiff,NULL,kernel,CV_MOP_CLOSE,1);
#endif
  
  nc = getConnectedComp(cdiff, &comp);
  // comment in for show diff image
  
  /**/
  IplImage* outImage = cvCloneImage(&srcImage);
  cvCvtColor(cdiff, outImage, CV_GRAY2RGB);
  CvSeq* c = comp;
  for(int i=0; i<nc && c!=0; i++){
    CvRect r = cvBoundingRect(c, 1);
    LOGI("contour=(%d,%d,%d,%d)", r.x, r.y, r.width, r.height);
    cvRectangle(outImage, POINTS(r), CV_RGB(255,255,0), 2, 8, 0);
    c = c->h_next;
  }
  /**/
  
  //cvCopy(outImage, &srcImage);

  //-------------------------------------------------------
  if (stage == DETECTION_MODE){
    if ((score = findEyePair(&srcImage, comp,nc,&eye1,&eye2,FaceBox[0])) > 0.01){
      //cvRectangle(cdiff,POINTS(eye1),cvScalarAll(255),1, 8, 0);
      //cvRectangle(cdiff,POINTS(eye2),cvScalarAll(255),1, 8, 0);
      //crop_rect(&srcImage, FaceBox[0],FaceImg[faces]);
      LOG("face score = %f\n", score);
      //if (score >SCTH) {

        //If you want to see the template enable the following line.
        sprintf(txt,"%1.3lf",score);
        CvPoint eyeCenter = CENTER(eye1);
        eyeCenter.y+=20;
        cvPutText(&srcImage, txt,
            eyeCenter,
            &font, CV_RGB(0,0,0));

        //DrawFace(FaceImg[faces]);

        //Draw Bounding boxes of eyes
        /*
        cvRectangle(&srcImage,POINTS(eye1),CV_RGB(255,0,0), 2,8,0);
        cvRectangle(&srcImage,POINTS(eye2),CV_RGB(255,0,0), 2,8,0);
         */
        cvRectangle(outImage,POINTS(eye1),CV_RGB(255,0,0), 2,8,0);
        cvRectangle(outImage,POINTS(eye2),CV_RGB(255,0,0), 2,8,0);
        
        //Draw Face Region on frame
        int NPTS[1] = {4};
        //cvPolyLine(&srcImage,FaceBox,NPTS,1,0,CV_RGB(0,255,0), 2,8,0);
        cvPolyLine(outImage,FaceBox,NPTS,1,0,CV_RGB(0,255,0), 2,8,0);
        
        //Show Detected Face Image
        //cvShowImage(wnd_face, FaceImg[faces]);

        //stage = TRACKING_MODE;
        timer=0;

        if (++faces>=FACES) {
          LOG("Detected face exceeds.\n");
          return;
        }
        
         LOG("------- %03d ----------\n",faces);
      //}
      //else{
      //   LOG("            ----->S=%lf\n",score);
      //}
    }
    
    LOG("%d faces detected", faces);

    // Show Diff Image 
    //cvShowImage(wnd_debug, cdiff);
    // Show Raw Image
    //cvShowImage(wnd_name, frame);

  } else if (stage == TRACKING_MODE) {
    //if (key == 'r' || (timer++>TIME_LIMIT)){
    //if (timer++>TIME_LIMIT) {
      stage = DETECTION_MODE;
    //}
  }
  cvCopy(outImage, &srcImage);
  /*
  if (nc < 10 && nc >= 2) {
    if (!cvSaveImage(outfile, outImage)) {
      LOG("failed to save image");
    }
  }
  */
  fcount=NEXT(fcount);
  return;
}

  /*
X0(u)=(1-u)*FB[0].x+u*FB[3].x
X1(u)=(1-u)*FB[1].x+u*FB[2].x

X(u,v)= X0(u)*(1-v) + X1(u)*v
  */
#define X(u,v) (((1-u)*FB[0].x+u*FB[3].x)*(1-v)+((1-u)*FB[1].x+u*FB[2].x)*v)
  /*
Y0(v)=(1-v)*FB[0].y+v*FB[1].y
Y1(v)=(1-v)*FB[3].y+v*FB[2].y

Y(u,v) = Y0(v)*(1-u) + Y1(v)*u
   */
#define Y(u,v) (((1-v)*FB[0].y+v*FB[1].y)*(1-u)+((1-v)*FB[3].y+v*FB[2].y)*u)

int BlinkDetector::crop_rect(IplImage *srcImage, CvPoint FB[],IplImage *RectImg) {
  int i,j,x,y;
  double u,v;
  uchar p[3];

  if (RectImg->nChannels ==1) {
    for (v=0,j=0; j<SIZY; j++,v+=(double)1/SIZY) {
      for (u=0, i=0; i<SIZX; i++,u+=(double)1/SIZX) {
        x=(int)X(u,v);
        y=(int)Y(u,v);
        p[0]=CV_IMAGE_ELEM(srcImage, uchar, y, x*3+0);
        p[1]=CV_IMAGE_ELEM(srcImage, uchar, y, x*3+1);
        p[2]=CV_IMAGE_ELEM(srcImage, uchar, y, x*3+2);
        RectImg->imageData[RectImg->widthStep*j + i] = (p[0]+p[1]+p[2])/3; 
      }
    }
    return 1;
  } else if (RectImg->nChannels ==3) {
    for (v=0,j=0; j<SIZY; j++,v+=(double)1/SIZY) {
      for (u=0, i=0; i<SIZX; i++,u+=(double)1/SIZX) {
        x=(int)X(u,v);
        y=(int)Y(u,v);
        p[0]=CV_IMAGE_ELEM(srcImage, uchar, y, x*3+0);
        p[1]=CV_IMAGE_ELEM(srcImage, uchar, y, x*3+1);
        p[2]=CV_IMAGE_ELEM(srcImage, uchar, y, x*3+2);
        RectImg->imageData[RectImg->widthStep*j + i*3] = p[0];
        RectImg->imageData[RectImg->widthStep*j+i*3+1] = p[1];
        RectImg->imageData[RectImg->widthStep*j+i*3+2] = p[2];
      }
    }
    return 1;
  }
  return 0;
}


int BlinkDetector::getConnectedComp(IplImage* diff, CvSeq** comp) {
  IplImage* _diff;
  int nc;

  //Since cvFindContours() destroyes first argument, we take copy beforhand.
  _diff = (IplImage*)cvClone(diff);
  // get connected comp
  nc = cvFindContours(_diff, storage, comp, sizeof(CvContour), 
          CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, 
          cvPoint(0,0));
  // Cloning allocates memory area. So, release the memory after that.
  cvReleaseImage(&_diff);
  // We don't need storage but comp and nc.
  cvClearMemStorage(storage);

  return nc;
}

double BlinkDetector::findEyePair(IplImage* srcImage, CvSeq* comp, int num, CvRect* eye1, CvRect* eye2, CvPoint *Fb) {
  CvRect r[20];
  int i,j;
  CvPoint eyepairs;
  double Score, MaxScore;

  if (num<2 || num>200) {
    LOG("not enough or too many contours %d", num);
    return 0.0;
  }

  for(i=0;i<min(num,20)&&comp !=0 ;){
    r[i] = cvBoundingRect(comp, 1);
    if ( is_eye(&r[i])) i++;
    comp = comp->h_next;
  }
  num = i;

  if (num<2) {
    LOG("not enough eye-like contours %d", num);
    return 0.0;
  }
  
  //LOG("Reduced to num=%d",num);

  // Select Most Likely Face Region
  MaxScore=0.;

  // check all permuataions
  for(i=0;i<num;i++){
    // LOG("r[i] (%d,%d)-(%d,%d)\n",r[i].x,r[i].y,r[i].x+r[i].width, r[i].y+r[i].height);
    for(j=i+1;j<num;j++){
      // LOG("r[j] (%d,%d)-(%d,%d)\n",r[j].x,r[j].y,r[j].x+r[j].width, r[j].y+r[j].height);
        LOG("check eye1=(%d,%d,%d,%d) eye2=(%d,%d,%d,%d)", r[i].x, r[i].y, r[i].width, r[i].height, r[j].x, r[j].y, r[j].width, r[j].height);
      if (is_eye(&r[j])==0) continue;
      if (is_eye_pair(&r[i],&r[j])==0) continue;

      // LOG("Convert\n");
      // if (convert_to_eye_region(&r[i], eye1)==0) continue;
      // if (convert_to_eye_region(&r[j], eye2)==0) continue;
      if (face_region((CvPoint)cvPoint(r[i].x+r[i].width/2,
                                                   r[i].y-r[i].height/2),
                (CvPoint)cvPoint(r[j].x+r[j].width/2,
                                                   r[j].y-r[j].height/2),Fb)
          ==0)  continue;

      crop_rect(srcImage, Fb, tmpface);
      Score=FaceScore(tmpface,mask);
      if (Score >MaxScore) {
        MaxScore = Score;
        //Does cvPoint Allocates Memory area? Check later.
        eyepairs = cvPoint(i,j);
      }
    }
  }

  if (MaxScore>0.001) {
    i=eyepairs.x;
    j=eyepairs.y;
    // LOG("Decided %d and %d ",i,j);
    face_region((CvPoint)cvPoint(r[i].x+r[i].width/2,
               r[i].y-r[i].height/2),
          (CvPoint)cvPoint(r[j].x+r[j].width/2,
               r[j].y-r[j].height/2),Fb);
                *eye1= r[i];
    *eye2= r[j];
    return Score;
  } else return 0.0;
}

#define Pt(x) (amp*(x-pmin)+pmin)
double BlinkDetector::FaceScore(IplImage *input, IplImage *msk)
{
  double sum1,sum2,sum3, amp;
  int i, j, pmax, pmin, Pi, Pm, bias;

// We don't use cvNormalize(input,input,255.0,0.0,CV_MINMAX,NULL)
// ,because we also need the mean pixel value.

  pmax=0;
  pmin = 255;
  sum1 = 0.0;
  for (j=0; j< input->height; j++) {
    for (i=0; i< input->width; i++) {
      Pi= (unsigned char)input->imageData[input->widthStep*j+i];
      if (Pi>pmax) pmax=Pi;
      else if (Pi<pmin) pmin=Pi;
      sum1 += Pi;
    }
  }

  bias = sum1/(SIZX*SIZY);
  amp=255./(pmax-pmin);
  bias=Pt(bias);

  //LOG("bias=%d\n",bias);

  sum1=0.0;
  sum2=0.0;
  sum3=0.0;
  for (j=0; j< input->height; j++) {
    for (i=0; i< input->width; i++) {
      Pm= (unsigned char) msk->imageData[msk->widthStep*j+i] - 128;
      Pi= (unsigned char) Pt(input->imageData[input->widthStep*j+i]) - bias;
      input->imageData[input->widthStep*j+i]=Pi+bias;
      sum2+= (double)Pi*Pi;
      if (Pm) {
        sum3+= (double)Pm*Pm;
        sum1 += ((double)Pi*Pm);
      }
    }
  }
  return sum1/(sqrt(sum3)*sqrt(sum2));
}

/**
 * 片目としての確からしさを判定する
 */
int BlinkDetector::is_eye(CvRect* eye) {
  /* the height must be shorter than the width */
  if ((double)eye->width/eye->height < EYE_RATIO){
    LOG("Eye Shape\n");
    return 0;
  }
  if (eye->width < 3 ||eye->width > IMAGE_W/5)  {
    LOG("Eye width is too short or too wide %d\n", eye->width);
    return 0;
  }
  return 1;
}

/**
 * 両目のペアの確からしさを判定する
 */
int BlinkDetector::is_eye_pair(CvRect* eye1, CvRect* eye2) {
  int idist=(int)dist(eye1,eye2);

  /* the width of the eyes are almost the same */
#ifdef SIMPLE
  if (abs(eye1->width - eye2->width) >= idist/2.)
#else
  if (abs(eye1->width - eye2->width) >= idist/6.)
#endif
  {
    LOG("Eye width similarity\n");
    return 0;
  }

  /* distance should be longer than 6 and shorter than IMAGE_H */
  if ((idist<10)||(idist>IMAGE_W/2)) {
    LOG("Eye distance is too small or too wide %d\n", idist);
    return 0;
  }
  /* distance should be longer than 6 and shorter than IMAGE_H */
  float ratio1 = (float)idist / (float)eye1->width;
  float ratio2 = (float)idist / (float)eye2->width;
  if (ratio1 < 2 || ratio2 < 2 || ratio1 > 10 || ratio2 > 10) {
    LOG("Eye distance/width is too small or too big %f %f\n", ratio1, ratio2);
    return 0;
  }
  /* the height f the eyes are about the same */
  if (abs(eye1->height - eye2->height) >= idist/8) {
    LOG("Eye height is too different %f\n", eye1->height - eye2->height);
    return 0;
  }
  /* vertical distance is smaller than the horizontal distance */
  if (fabs(eye1->y - eye2->y) >= fabs(eye1->x - eye2->x) ) {
    LOG("Eye Slant angle\n");
    return 0;
  }
  return 1;
}


int BlinkDetector::face_region(CvPoint Leye, CvPoint Reye, CvPoint FB[])
{
  CvPoint tmp;
  double hd, vd, theta, cost, sint, d, a, b;
  int i;

  // Swap Left <---> Right, if necessary.
  if (Leye.x > Reye.x) {
    tmp = Leye;
    Leye = Reye;
    Reye = tmp;
  }

  //Compute Parameters
  hd = Reye.x - Leye.x;
  vd = Reye.y - Leye.y;
  theta=atan(vd/hd);
  d=sqrt(hd*hd+vd*vd);

  cost=cos(theta);
  sint=sin(theta);
 
  /* 0
        d     d 
     - ---,  --- + Left Eye position
        2     3
  */
  a = - d/2.0;
  b = - d/3.0;

  FB[0].x= a*cost -b*sint + Leye.x;
  FB[0].y= a*sint +b*cost + Leye.y;

  /* 3
        d     d 
       ---,  --- + Right Eye position
        2     2
  */
  a =  d/2.0;
  b =  - d/3.0;

  FB[3].x= a*cost -b*sint + Reye.x;
  FB[3].y= a*sint +b*cost + Reye.y;

  /* 1
        d     3d 
     - ---, - --- + Left Eye position
        2      2
  */
  a = -d/2.0;
  b = 4.0*d/2.0;

  FB[1].x= a*cost -b*sint + Leye.x;
  FB[1].y= a*sint +b*cost + Leye.y;

  /* 2
       d     3d 
      ---, - --- + Right Eye position
       2      2
  */
  a = d/2.0;
  b = 4.0*d/2.0;

  FB[2].x= a*cost -b*sint + Reye.x;
  FB[2].y= a*sint +b*cost + Reye.y;

  for (i=0; i<4; i++) {
    if ((FB[i].x <0)||(FB[i].x >IMAGE_W)) return 0;
    if ((FB[i].y <0)||(FB[i].y >IMAGE_H)) return 0;
  }
  /*
  for (i=0; i<4; i++) {
    LOG("(%d,%d)",FB[i].x,FB[i].y);
  }
  */

  return 1;
}

void BlinkDetector::DrawFace(IplImage *F) {
  //Left hair
  cvRectangle(F,cvPoint(0,0),cvPoint(4,4),cvScalarAll(0),CV_FILLED,8,0);
  //Right hair
  cvRectangle(F,cvPoint(59,0),cvPoint(63,4),cvScalarAll(0),CV_FILLED,8,0);
  //Left eye
  cvRectangle(F,cvPoint(10,17),cvPoint(19,25),cvScalarAll(0),CV_FILLED,8,0);
  //Right eye
  cvRectangle(F,cvPoint(45,17),cvPoint(54,25),cvScalarAll(0),CV_FILLED,8,0);
  //Left cheek
  cvRectangle(F,cvPoint(10,35),cvPoint(18,45),cvScalarAll(255),CV_FILLED,8,0);
  //Right cheek
  cvRectangle(F,cvPoint(46,35),cvPoint(54,45),cvScalarAll(255),CV_FILLED,8,0);
  //Nose ridge
  cvRectangle(F,cvPoint(29,27),cvPoint(34,37),cvScalarAll(255),CV_FILLED,8,0);
  //Left nose hole
  //cvRectangle(F,cvPoint(26,44),cvPoint(30,48),cvScalarAll(0),CV_FILLED,8,0);
  //Right nose hole
  //cvRectangle(F,cvPoint(33,44),cvPoint(37,48),cvScalarAll(0),CV_FILLED,8,0);
  //Mouth
  //cvRectangle(F,cvPoint(22,58),cvPoint(40,62),cvScalarAll(0),CV_FILLED,8,0);
}
