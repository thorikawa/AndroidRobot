/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.4
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.polysfactory.facerecognition.jni;


public class FaceCrop {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  public FaceCrop(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  public static long getCPtr(FaceCrop obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        NativeFaceCropJNI.delete_FaceCrop(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public FaceCrop(String basedir) {
    this(NativeFaceCropJNI.new_FaceCrop(basedir), true);
  }

  public boolean crop(String inFile) {
    return NativeFaceCropJNI.FaceCrop_crop(swigCPtr, this, inFile);
  }

}
