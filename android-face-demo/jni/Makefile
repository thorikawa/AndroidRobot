LIBS = -L/usr/local/opencv-2.3.0/lib -lopencv_core -lopencv_highgui -lopencv_features2d -lopencv_imgproc -lopencv_objdetect -lopencv_legacy
CFLAGS = -I/usr/local/opencv-2.3.0/include
DEFS = -DSIMPLE

dump: HaarFaceDetector.o FaceFeatureDump.o
	g++ -o dump $(LIBS) FaceFeatureDump.o HaarFaceDetector.o
match: FaceFeatureMatch.o HaarFaceDetector.o ObjectMatcher.o
	g++ -o match $(LIBS) FaceFeatureMatch.o HaarFaceDetector.o ObjectMatcher.o
eigen: EigenFaceCUI.cpp HaarFaceDetector.o
	g++ $(LIBS) $(CFLAGS) EigenFaceCUI.cpp -o eigen
crop: FaceCrop.cpp HaarFaceDetector.o
	g++ $(CFLAGS) -c FaceCrop.cpp -o FaceCrop.o
	g++ $(LIBS) $(CFLAGS) FaceCrop.o HaarFaceDetector.o -o crop
FaceFeatureDump.o: FaceFeatureDump.cpp
	g++ $(CFLAGS) -c FaceFeatureDump.cpp -o FaceFeatureDump.o
FaceFeatureMatch.o: FaceFeatureMatch.cpp
	g++ $(CFLAGS) -c FaceFeatureMatch.cpp -o FaceFeatureMatch.o
HaarFaceDetector.o: HaarFaceDetector.cpp
	g++ $(CFLAGS) -c HaarFaceDetector.cpp -o HaarFaceDetector.o
ObjectMatcher.o: ObjectMatcher.cpp
	g++ $(CFLAGS) -c ObjectMatcher.cpp -o ObjectMatcher.o
clean:
	rm -f a.out FaceFeatureDump.o HaarFaceDetector.o FaceFeatureMatch.o ObjectMatcher.o
