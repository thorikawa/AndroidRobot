#########################################################
#c flags, included, and lib dependencies
#########################################################

#notice the "recycling" of CMAKE_C_FLAGS
#this is necessary to pick up android flags
set( CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wall -pedantic -fPIC" )
set( ST_NO_EXCEPTION_HANDLING 1)

INCLUDE_DIRECTORIES(${CMAKE_CURRENT_SOURCE_DIR})
include_directories(include)

#set( LIBRARY_DEPS ${AndroidOpenCV_LIBS} ${OpenCV_LIBS} )
if(ANDROID)
  set( LIBRARY_DEPS ${LIBRARY_DEPS} log dl)
endif(ANDROID)

#########################################################
#SWIG STUFF
#########################################################
#the java package to place swig generated java files in
set(MY_PACKAGE com.polysfactory.robotaudio.jni)

if(NOT ANDROID)
  #non android swig and jni
  #jni is available by default on android
  find_package(JNI REQUIRED)
  include_directories(${JNI_INCLUDE_DIRS})
  FIND_PACKAGE(SWIG)
endif()

INCLUDE(${SWIG_USE_FILE}) #on android this is found by the cmake toolchain

if(ANDROID)
  #this will set the output path for the java package
  #and properly create the package declarations in generated java sources
  SET_SWIG_JAVA_PACKAGE( ${MY_PACKAGE} ) #defined in the android toolchain
endif(ANDROID)

#this add's the swig path for the opencv wrappers
#SET(CMAKE_SWIG_FLAGS ${CMAKE_SWIG_FLAGS} "-I${AndroidOpenCV_SWIG_DIR}" )

SET_SOURCE_FILES_PROPERTIES(NativeSoundTouch.i PROPERTIES CPLUSPLUS ON)

#add the swig module, giving it the name, java, and then all of the source files
SWIG_ADD_MODULE(SoundTouch java 
                NativeSoundTouch.i #swig file
                AAFilter.cpp BPMDetect.cpp FIFOSampleBuffer.cpp FIRFilter.cpp PeakFinder.cpp RateTransposer.cpp SoundTouch.cpp TDStretch.cpp cpu_detect_x86_gcc.cpp mmx_optimized.cpp sse_optimized.cpp WavFile.cpp RobotAudio.cpp 
)

#link the module like any other
target_link_libraries(SoundTouch ${LIBRARY_DEPS} )
