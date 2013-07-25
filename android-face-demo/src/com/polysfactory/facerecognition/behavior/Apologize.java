package com.polysfactory.facerecognition.behavior;

import android.util.Log;

import com.polysfactory.facerecognition.App;

public class Apologize extends Behavior {

    @Override
    public void execute() {
        Log.d(App.TAG, "apologize");
        mUsbCommander.lightLed(1, 1, 1);
        int n = (int) (3 * Math.random());
        String msg = null;
        switch (n) {
        case 0:
            msg = "ごめんなさい";
            break;
        case 1:
            msg = "すいませんでした";
            break;
        case 2:
            msg = "申し訳ありません";
            break;
        default:
            break;
        }
        mAudioCommander.speakByRobotVoie(msg);
        sleep(1000);
        mUsbCommander.lightLed(0);
    }

}
