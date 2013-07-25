#include <EigenFace.h>

EigenFace::EigenFace(char* trainingDataFile) {
  // load train data
  memccpy(mTrainingDataFile, trainingDataFile, '\0', 256);
  return;
}

/**
 * 顔認識する対象の画像を事前加工する
 * (1)同一サイズへのリサイズ
 * (2)ヒストグラムの正規化
 */
IplImage* EigenFace::preProcess(IplImage* testFace) {
  IplImage* tmpImage = cvCreateImage(cvSize(FACE_WIDTH, FACE_HEIGHT), IPL_DEPTH_8U, 1);
  IplImage* resizedFaceImage = cvCloneImage(tmpImage);
  cvResize(testFace, tmpImage, CV_INTER_AREA);
  cvEqualizeHist(tmpImage, resizedFaceImage);
  cvReleaseImage(&tmpImage);
  return resizedFaceImage;
}

/**
 * 顔を学習する
 */
void EigenFace::learn(char* trainFileName)
{
	int i, offset;
  
	// load training data
	nTrainFaces = loadFaceImgArray(trainFileName);
	if( nTrainFaces < 2 )
	{
		LOGE("Need 2 or more training faces\n"
         "Input file contains only %d\n", nTrainFaces);
		return;
	}
  
	// do PCA on the training faces
	doPCA();
  
	// project the training images onto the PCA subspace
	projectedTrainFaceMat = cvCreateMat( nTrainFaces, nEigens, CV_32FC1 );
	offset = projectedTrainFaceMat->step / sizeof(float);
	for(i=0; i<nTrainFaces; i++)
	{
		//int offset = i * nEigens;
		cvEigenDecomposite(
                       faceImgArr[i],
                       nEigens,
                       eigenVectArr,
                       0, 0,
                       pAvgTrainImg,
                       //projectedTrainFaceMat->data.fl + i*nEigens);
                       projectedTrainFaceMat->data.fl + i*offset);
	}
  
	// store the recognition data as an xml file
	storeTrainingData();
}

/**
 * 顔を認識して、該当する人のIDを返す
 */
int EigenFace::recognize(IplImage* testFace)
{
  // 事前加工
  IplImage* resizedFaceImage = preProcess(testFace);
  
  float * projectedTestFace = 0;
  
  // project the test images onto the PCA subspace
  projectedTestFace = (float *)cvAlloc( nEigens*sizeof(float) );
  int iNearest, nearest;

  // project the test image onto the PCA subspace
  cvEigenDecomposite(
    resizedFaceImage,
    nEigens,
    eigenVectArr,
    0, 0,
    pAvgTrainImg,
    projectedTestFace);

  iNearest = findNearestNeighbor(projectedTestFace);
  nearest  = trainPersonNumMat->data.i[iNearest];
  
  cvReleaseImage(&resizedFaceImage);

  return nearest;
}

//////////////////////////////////
// loadTrainingData()
//
int EigenFace::loadTrainingData()
{
  LOGD("load from %s", mTrainingDataFile);
	CvFileStorage * fileStorage;
	int i;
  
	// create a file-storage interface
	fileStorage = cvOpenFileStorage( mTrainingDataFile, 0, CV_STORAGE_READ );
	if( !fileStorage )
	{
		LOGE("Can't open facedata.xml\n");
		return 0;
	}
  
	nEigens = cvReadIntByName(fileStorage, 0, "nEigens", 0);
	nTrainFaces = cvReadIntByName(fileStorage, 0, "nTrainFaces", 0);
	trainPersonNumMat = (CvMat *)cvReadByName(fileStorage, 0, "trainPersonNumMat", 0);
	eigenValMat  = (CvMat *)cvReadByName(fileStorage, 0, "eigenValMat", 0);
	projectedTrainFaceMat = (CvMat *)cvReadByName(fileStorage, 0, "projectedTrainFaceMat", 0);
	pAvgTrainImg = (IplImage *)cvReadByName(fileStorage, 0, "avgTrainImg", 0);
	eigenVectArr = (IplImage **)cvAlloc(nTrainFaces*sizeof(IplImage *));
	for(i=0; i<nEigens; i++)
	{
		char varname[200];
		sprintf( varname, "eigenVect_%d", i );
		eigenVectArr[i] = (IplImage *)cvReadByName(fileStorage, 0, varname, 0);
	}
  
	// release the file-storage interface
	cvReleaseFileStorage( &fileStorage );
  
	return 1;
}

//////////////////////////////////
// storeTrainingData()
//
void EigenFace::storeTrainingData()
{
	CvFileStorage * fileStorage;
	int i;
  
	// create a file-storage interface
	fileStorage = cvOpenFileStorage( mTrainingDataFile, 0, CV_STORAGE_WRITE );
  
	// store all the data
	cvWriteInt( fileStorage, "nEigens", nEigens );
	cvWriteInt( fileStorage, "nTrainFaces", nTrainFaces );
	cvWrite(fileStorage, "trainPersonNumMat", personNumTruthMat, cvAttrList(0,0));
	cvWrite(fileStorage, "eigenValMat", eigenValMat, cvAttrList(0,0));
	cvWrite(fileStorage, "projectedTrainFaceMat", projectedTrainFaceMat, cvAttrList(0,0));
	cvWrite(fileStorage, "avgTrainImg", pAvgTrainImg, cvAttrList(0,0));
	for(i=0; i<nEigens; i++)
	{
		char varname[200];
		sprintf( varname, "eigenVect_%d", i );
		cvWrite(fileStorage, varname, eigenVectArr[i], cvAttrList(0,0));
	}
  
	// release the file-storage interface
	cvReleaseFileStorage( &fileStorage );
}

//////////////////////////////////
// findNearestNeighbor()
//
int EigenFace::findNearestNeighbor(float * projectedTestFace)
{
  //double leastDistSq = 1e12;
  double leastDistSq = DBL_MAX;
  int i, iTrain, iNearest = 0;

  for(iTrain=0; iTrain<nTrainFaces; iTrain++)
  {
    double distSq=0;

    for(i=0; i<nEigens; i++)
    {
      float d_i =
        projectedTestFace[i] -
        projectedTrainFaceMat->data.fl[iTrain*nEigens + i];
      distSq += d_i*d_i / eigenValMat->data.fl[i];  // Mahalanobis
      //distSq += d_i*d_i; // Euclidean
    }
    LOGI("EigenFace:Dist(%d): %f", trainPersonNumMat->data.i[iTrain], distSq);
    if(distSq < leastDistSq)
    {
      leastDistSq = distSq;
      iNearest = iTrain;
    }
  }
  LOGI("EigenFace:NearestDist: %f", leastDistSq);

  return iNearest;
}

//////////////////////////////////
// doPCA()
//
void EigenFace::doPCA()
{
	int i;
	CvTermCriteria calcLimit;
	CvSize faceImgSize;
  
	// set the number of eigenvalues to use
	nEigens = nTrainFaces-1;
  
	// allocate the eigenvector images
	faceImgSize.width  = faceImgArr[0]->width;
	faceImgSize.height = faceImgArr[0]->height;
	eigenVectArr = (IplImage**)cvAlloc(sizeof(IplImage*) * nEigens);
	for(i=0; i<nEigens; i++)
		eigenVectArr[i] = cvCreateImage(faceImgSize, IPL_DEPTH_32F, 1);
  
	// allocate the eigenvalue array
	eigenValMat = cvCreateMat( 1, nEigens, CV_32FC1 );
  
	// allocate the averaged image
	pAvgTrainImg = cvCreateImage(faceImgSize, IPL_DEPTH_32F, 1);
  
	// set the PCA termination criterion
	calcLimit = cvTermCriteria( CV_TERMCRIT_ITER, nEigens, 1);
  
	// compute average image, eigenvalues, and eigenvectors
	cvCalcEigenObjects(
                     nTrainFaces,
                     (void*)faceImgArr,
                     (void*)eigenVectArr,
                     CV_EIGOBJ_NO_CALLBACK,
                     0,
                     0,
                     &calcLimit,
                     pAvgTrainImg,
                     eigenValMat->data.fl);
  
	cvNormalize(eigenValMat, eigenValMat, 1, 0, CV_L1, 0);
}


//////////////////////////////////
// loadFaceImgArray()
//
int EigenFace::loadFaceImgArray(char * filename)
{
	FILE * imgListFile = 0;
	char imgFilename[512];
	int iFace, nFaces=0;
  
  
	// open the input file
	if( !(imgListFile = fopen(filename, "r")) )
	{
		LOGE("Can\'t open file %s\n", filename);
		return 0;
	}
  
	// count the number of faces
	while( fgets(imgFilename, 512, imgListFile) ) ++nFaces;
	rewind(imgListFile);
  
	// allocate the face-image array and person number matrix
	faceImgArr        = (IplImage **)cvAlloc( nFaces*sizeof(IplImage *) );
	personNumTruthMat = cvCreateMat( 1, nFaces, CV_32SC1 );
  
	// store the face images in an array
	for(iFace=0; iFace<nFaces; iFace++)
	{
		// read person number and name of image file
		fscanf(imgListFile,
           "%d %s", personNumTruthMat->data.i+iFace, imgFilename);
    
		// load the face image
    LOGD("load from %s", imgFilename);
    IplImage* tmpImage = cvLoadImage(imgFilename, CV_LOAD_IMAGE_GRAYSCALE);
    faceImgArr[iFace] = cvCloneImage(tmpImage);
    cvEqualizeHist(tmpImage, faceImgArr[iFace]);
    
		if( !faceImgArr[iFace] )
		{
			LOGE("Can\'t load image from %s\n", imgFilename);
			return 0;
		}
	}
  
	fclose(imgListFile);
  
	return nFaces;
}