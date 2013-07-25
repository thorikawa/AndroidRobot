#include "ObjectMatcher.h"
#include <fstream>
#include "log.h"

/**
 * 特徴ベクトルの読み込み
 */
bool ObjectMatcher::loadDescription(const char* filename) {
	
	LOGD("load desc: %s", filename);
	
	ifstream descFile(filename);
	if (descFile.fail()) {
		LOGE("file open error");
		return false;
	}
	
	int num = 0;
	string line;
	while (getline(descFile, line, '\n')) {
		num++;
	}
	CvMat* objMat = cvCreateMat(num, DIM, CV_32FC1);
	descFile.clear();
	descFile.seekg(0);
	
	int cur = 0;
	while (getline(descFile, line, '\n')) {
		vector<string> ldata;
		istringstream ss(line);
		string s;
		while (getline(ss, s, '\t')) {
			ldata.push_back(s);
		}
		for (int i=0; i<DIM; i++) {
			float val = atof(ldata[i].c_str());
			CV_MAT_ELEM(*objMat, float, cur, i) = val;
		}
		cur++;
	}
	descFile.close();
	
	cvLSHAdd(lsh, objMat);
	
	objNumber++;
	lastIndex += objMat->rows;
	lastIndexes.push_back(lastIndex);
	numKeypoints.push_back(objMat->rows);
	
	LOGD("load row: %d", cur);
	
	
	return true;
}

/**
 * コンストラクタ
 */
ObjectMatcher::ObjectMatcher() :
objNumber(0),
lastIndex(-1)
{
	lsh = cvCreateMemoryLSH(DIM, 100000, 5, 64, CV_32FC1);
}

/**
 * デコンストラクタ
 */
ObjectMatcher::~ObjectMatcher()
{
	cvReleaseLSH(&lsh);
}

int ObjectMatcher::getObjectId (int index) {
	int i;
	for (i=0; i<objNumber; i++) {
		if (index <= lastIndexes[i]) {
			break;
		}
	}
	return i;
}

/**
 * オブジェクトマッチング
 */
//void KmeansTracker::setInitial (IplImage* currentFrame, CvBox2D ellipse, vector<CvPoint> targetCenterPoints) {
int ObjectMatcher::match (IplImage* queryImage) {
	if (queryImage == NULL) {
		LOGE("no image!");
		return -1;
	}
	
	// クエリからSURF特徴量を抽出
	CvSeq *queryKeypoints = 0;
	CvSeq *queryDescriptors = 0;
	CvMemStorage *storage = cvCreateMemStorage(0);
	CvSURFParams params = cvSURFParams(500, 1);
	cvExtractSURF(queryImage, 0, &queryKeypoints, &queryDescriptors, storage, params);
	LOGD("%d keypoints detected", queryDescriptors->total);
	
	// 投票箱を用意
	vector<int> flag(LSHSize(lsh));
	vector<int> votes(objNumber);
	
	// クエリのキーポイントの特徴ベクトルをCvMatに展開
	CvMat* queryMat = cvCreateMat(queryDescriptors->total, DIM, CV_32FC1);
	CvSeqReader reader;
	float* ptr = queryMat->data.fl;
	cvStartReadSeq(queryDescriptors, &reader);
	for (int i = 0; i < queryDescriptors->total; i++) {
		float* descriptor = (float*)reader.ptr;
		CV_NEXT_SEQ_ELEM(reader.seq->elem_size, reader);
		memcpy(ptr, descriptor, DIM * sizeof(float));  // DIM次元の特徴ベクトルをコピー
		ptr += DIM;
	}
	
	// LSHで1-NNのキーポイントインデックスを検索
	int k = 1;  // k-NNのk
	CvMat* indices = cvCreateMat(queryKeypoints->total, k, CV_32SC1);   // 1-NNのインデックス
	CvMat* dists = cvCreateMat(queryKeypoints->total, k, CV_64FC1);     // その距離
	cvLSHQuery(lsh, queryMat, indices, dists, k, 100);
	cvReleaseMat(&queryMat);
	
	// 1-NNキーポイントを含む物体に得票
	LOGD("check %d nns", indices->rows);
	for (int i = 0; i < indices->rows; i++) {
		int idx = CV_MAT_ELEM(*indices, int, i, 0);
		double dist = CV_MAT_ELEM(*dists, double, i, 0);
		if (idx < 0) {
			// can't find nn
			continue;
		}
		
		if (!flag[idx] && dist < THRESHOLD) {
			int id = getObjectId(idx);
			votes[id]++;
			flag[idx] = 1;
		}
	}  
	LOGD("end: find nn");
	
	// 投票数が最大の物体IDを求める
	int maxId = -1;
	int maxVal = -1;
	for (int i = 0; i < objNumber; i++) {
		LOGD("vote for %d: %d", i, votes[i]);    
		if (votes[i] > maxVal) {
			maxId = i;
			maxVal = votes[i];
		}
	}
	LOGD("this object is %d", maxId);
	
	// 時間計測
	// tt = (double)cvGetTickCount() - tt;
	// cout << "Recognition Time = " << tt / (cvGetTickFrequency() * 1000.0) << "ms" << endl;
	
  /*
	if (maxVal > numKeypoints[maxId]/10) {
		for (int i = 0; i < indices->rows; i++) {
			int idx = CV_MAT_ELEM(*indices, int, i, 0);
			double dist = CV_MAT_ELEM(*dists, double, i, 0);
			if (idx < 0) {
				// can't find nn
				continue;
			}
			
			if (dist < THRESHOLD) {
				int id = getObjectId(idx);
				if (id == maxId) {
					CvSURFPoint* spt = (CvSURFPoint*)cvGetSeqElem(queryKeypoints, i);
					//circle(img, spt->pt, 3, cvScalar(255, 0, 255, 0));
				}
			}
		}
	} else {
		maxId = -1;
	}
   */

  for (int i = 0; i < indices->rows; i++) {
    int idx = CV_MAT_ELEM(*indices, int, i, 0);
    double dist = CV_MAT_ELEM(*dists, double, i, 0);
    if (idx < 0) {
      // can't find nn
      continue;
    }
    
    if (dist < THRESHOLD) {
      int id = getObjectId(idx);
      if (id == maxId) {
        CvSURFPoint* spt = (CvSURFPoint*)cvGetSeqElem(queryKeypoints, i);
        cvCircle(queryImage, cvPoint((int)spt->pt.x, (int)spt->pt.y), 3, cvScalar(255, 0, 255, 0));
      }
    }
  }
  
	// 後始末
	cvReleaseMat(&indices);
	cvReleaseMat(&dists);
	cvClearSeq(queryKeypoints);
	cvClearSeq(queryDescriptors);
	cvReleaseMemStorage(&storage);
	
	return maxId;
}