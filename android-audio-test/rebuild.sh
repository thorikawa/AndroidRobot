#!/bin/sh

rm -rf build
mkdir build
cd build
cmake -DCMAKE_TOOLCHAIN_FILE=../android.toolchain.cmake -DST_NO_EXCEPTION_HANDLING=1 ..
make -j6

