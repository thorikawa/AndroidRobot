package com.polysfactory.facerecognition.behavior;

import android.util.Log;

import com.polysfactory.facerecognition.App;

public class Shy extends Behavior {

    @Override
    public void execute() {
        Log.d(App.TAG, "shy");
        mUsbCommander.lightLed(0, 3, 3);
        mAudioCommander.speakByRobotVoie("いやー、それほどでも");
        sleep(1000);
        mUsbCommander.lightLed(0);
    }

}
