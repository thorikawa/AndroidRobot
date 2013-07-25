#include <stdexcept>
#include <stdio.h>
#include <string.h>
#include "WavFile.h"
#include "SoundTouch.h"

using namespace soundtouch;
using namespace std;

// Processing chunk size
//#define BUFF_SIZE           2048
#define BUFF_SIZE           256

// Not needed for GNU environment... 
// #define SET_STREAM_TO_BIN_MODE(f) {}

namespace soundtouch {
  class RobotAudio {
  public:
    RobotAudio(char* inFile, char* outFie);
    int pitchShift(float pitch);
  private:
    char mInFile[256];
    char mOutFile[256];
    void openFiles(WavInFile **inFile, WavOutFile **outFile);
    void setup(SoundTouch *pSoundTouch, const WavInFile *inFile, float pitch);
    void process(SoundTouch *pSoundTouch, WavInFile *inFile, WavOutFile *outFile);
  };
}
