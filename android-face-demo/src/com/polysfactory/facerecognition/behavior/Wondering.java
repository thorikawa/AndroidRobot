package com.polysfactory.facerecognition.behavior;

import android.util.Log;

import com.polysfactory.facerecognition.App;
import com.polysfactory.facerecognition.CommandUtils;

/**
 * あいさつの振る舞い<br>
 * @author $Author: horikawa.takahiro@gmail.com $
 * @version $Revision: 61 $
 */
public class Wondering extends Behavior {

    int eyeColor = 10;

    @Override
    public void execute() {
        Log.d(App.TAG, "Greeting::action");
        for (int i = 0; i < 20; i++) {
            CommandUtils.randomEye(mUsbCommander);
            sleep(100);
            CommandUtils.randomHand(mUsbCommander);
            sleep(100);
            CommandUtils.randomMove(mUsbCommander);
            sleep(5000);
        }
    }

}
