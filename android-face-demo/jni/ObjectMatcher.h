#ifndef OBJECT_MATCHER_H_
#define OBJECT_MATCHER_H_

#include <opencv2/opencv.hpp>
#include <vector>

const double THRESHOLD = 0.3;

using namespace std;

class ObjectMatcher {
public:
	ObjectMatcher();
	virtual ~ObjectMatcher();
	/** 指定された画像の特徴点を検出し、LSH特定で物体認識を行う */
	int match(IplImage* queryImage);
	/** 指定されたファイル名から特徴ベクトルをLSHメモリハッシュに読み込む */
	bool loadDescription(const char* filename);
private:  
	int getObjectId (int index);
	/** 検出する特徴ベクトルの次元数 */
	static const int DIM = 128;
	/** LSHメモリハッシュ */
	CvLSH* lsh;
	/** 追加された物体の数 */
	int objNumber;
	/** 特徴点累計の最後の添字 */
	int lastIndex;
	/** 各オブジェクトにおける最後の特徴点累計添字 */
	vector<int> lastIndexes;
	/** 各オブジェクトの特徴点数 */
	vector<int> numKeypoints;
};

#endif /* OBJECT_MATCHER_H_ */