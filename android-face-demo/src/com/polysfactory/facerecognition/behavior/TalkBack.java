package com.polysfactory.facerecognition.behavior;

import android.util.Log;

import com.polysfactory.facerecognition.App;

public class TalkBack extends Behavior {

    @Override
    public void execute() {
        Log.d(App.TAG, "talkback");
        mUsbCommander.lightLed(3, 0, 0);
        mAudioCommander.speakByRobotVoie("誰に向かって口聞いてんだよ！");
        sleep(2000);
        mUsbCommander.lightLed(0);
    }

}
