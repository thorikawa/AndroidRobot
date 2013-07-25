/* File : BlinkModule.i */
%module BlinkModule

/*
 * the java import code muse be included for the opencv jni wrappers
 * this means that the android project must reference opencv/android as a project
 * see the default.properties for how this is done
 */
%pragma(java) jniclassimports=%{
import com.opencv.jni.*; //import the android-opencv jni wrappers
%}

%pragma(java) jniclasscode=%{
	static {
		try {
			//load the cvcamera library, make sure that libcvcamera.so is in your <project>/libs/armeabi directory
			//so that android sdk automatically installs it along with the app.
			
			//the android-opencv lib must be loaded first inorder for the cvcamera
			//lib to be found
			//check the apk generated, by opening it in an archive manager, to verify that
			//both these libraries are present
			System.loadLibrary("android-opencv");
      System.loadLibrary("BlinkDetector");
		} catch (UnsatisfiedLinkError e) {
			//badness
			throw e;
		}
	}

%}

%{
#include "image_pool.h"
#include "BlinkDetector.h"
using namespace cv;
%}


//import the android-cv.i file so that swig is aware of all that has been previous defined
//notice that it is not an include....
%import "android-cv.i"

//sample jni class
%typemap(javaimports) BlinkDetector "
import com.opencv.jni.image_pool;// import the image_pool interface for playing nice with android-opencv
"
class BlinkDetector {
public:
  BlinkDetector();
	void findFace (int input_idx, image_pool* pool);
};
