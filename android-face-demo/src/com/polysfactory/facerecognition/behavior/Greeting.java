package com.polysfactory.facerecognition.behavior;

import android.util.Log;

import com.polysfactory.facerecognition.App;
import com.polysfactory.facerecognition.CommandUtils;

/**
 * あいさつの振る舞い<br>
 * @author $Author: horikawa.takahiro@gmail.com $
 * @version $Revision: 69 $
 */
public class Greeting extends Behavior {

    int n = 0;

    @Override
    public void execute() {
        Log.d(App.TAG, "Greeting::action");
        CommandUtils.randomEye(mUsbCommander);
        sleep(100);
        if (n == 1) {
            mUsbCommander.rotateRightHand(60);
        } else {
            mUsbCommander.rotateLeftHand(60);
        }
        mAudioCommander.speakByRobotVoie("こんにちは");
        mUsbCommander.lightLed(0, 3, 0);
        sleep(100);
        if (n == 1) {
            mUsbCommander.rotateRightHand(10);
        } else {
            mUsbCommander.rotateLeftHand(10);
        }
        sleep(1000);
        mUsbCommander.lightLed(0, 0, 0);
        n = (n + 1) % 2;
    }

}
