package com.polysfactory.facerecognition.behavior;

import android.util.Log;

import com.polysfactory.facerecognition.App;

public class GreetToPersonBehavior extends Behavior {
    String name;

    @Override
    public void execute() {
        mBehaviorInfo.canInterrupt = false;
        Log.d(App.TAG, "Greeting::action");
        mUsbCommander.lightLed(0, 0, 3);
        sleep(100);
        for (int i = 0; i < 3; i++) {
            mUsbCommander.rotateNeck(0);
            sleep(300);
            mUsbCommander.rotateNeck(63);
            sleep(300);
        }
        mUsbCommander.rotateNeck(32);
        mAudioCommander.speakByRobotVoie("こんにちわ、" + name + "さん！");
        sleep(1000);
        mUsbCommander.lightLed(0, 0, 0);
    }
}
