/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.4
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.polysfactory.facerecognition.jni;

import com.opencv.jni.image_pool;// import the image_pool interface for playing nice with android-opencv

public class BlinkDetector {
  private long swigCPtr;
  protected boolean swigCMemOwn;
  public BlinkDetector(long cPtr, boolean cMemoryOwn) {
	swigCMemOwn = cMemoryOwn;
	swigCPtr = cPtr;
  }
  public static long getCPtr(BlinkDetector obj) {
	return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        BlinkModuleJNI.delete_BlinkDetector(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public BlinkDetector() {
    this(BlinkModuleJNI.new_BlinkDetector(), true);
  }

  public void findFace(int input_idx, image_pool pool) {
    BlinkModuleJNI.BlinkDetector_findFace(swigCPtr, this, input_idx, image_pool.getCPtr(pool), pool);
  }

}
