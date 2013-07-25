package com.polysfactory.facerecognition.behavior;

import android.util.Log;

import com.polysfactory.facerecognition.App;

/**
 * あいさつの振る舞い<br>
 * @author $Author: horikawa.takahiro@gmail.com $
 * @version $Revision: 61 $
 */
public class BataBata extends Behavior {

    @Override
    public void execute() {
        Log.d(App.TAG, "BataBata::action");
        for (int i = 0; i < 5; i++) {
            mUsbCommander.rotateLeftHand(63);
            sleep(100);
            mUsbCommander.rotateRightHand(0);
            sleep(600);
            mUsbCommander.rotateLeftHand(0);
            sleep(100);
            mUsbCommander.rotateRightHand(63);
            sleep(600);
        }
        mUsbCommander.rotateLeftHand(10);
        sleep(100);
        mUsbCommander.rotateRightHand(10);
        sleep(100);
    }

}
