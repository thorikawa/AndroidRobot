package com.polysfactory.facerecognition.behavior;

import android.util.Log;

import com.polysfactory.facerecognition.App;

/**
 * ウロウロする振る舞い<br>
 * @author $Author: horikawa.takahiro@gmail.com $
 * @version $Revision: 70 $
 */
public class KyoroKyoro extends Behavior {

    @Override
    public void execute() {
        Log.d(App.TAG, "KyoroKyoro::action");
        mUsbCommander.lightLed(0, 3, 0);
        sleep(100);
        mUsbCommander.rotateNeck(63);
        sleep(700);
        mUsbCommander.lightLed(2, 2, 2);
        sleep(100);
        mUsbCommander.rotateNeck(32);
        sleep(700);
        mUsbCommander.stop();
        sleep(100);
        mUsbCommander.lightLed(0, 0, 3);
        sleep(100);
        mUsbCommander.lightLed(0, 0, 0);
        sleep(100);
    }

}
