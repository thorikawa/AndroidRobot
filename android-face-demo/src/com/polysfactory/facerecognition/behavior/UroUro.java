package com.polysfactory.facerecognition.behavior;

import android.util.Log;

import com.polysfactory.facerecognition.App;
import com.polysfactory.facerecognition.CommandUtils;

/**
 * ウロウロする振る舞い<br>
 * @author $Author: horikawa.takahiro@gmail.com $
 * @version $Revision: 65 $
 */
public class UroUro extends Behavior {

    @Override
    public void execute() {
        Log.d(App.TAG, "UroUro::action");
        CommandUtils.randomEye(mUsbCommander);
        sleep(100);
        for (int i = 0; i < 3; i++) {
            CommandUtils.randomMove(mUsbCommander);
            sleep(1000);
        }
        mUsbCommander.stop();
        sleep(100);
        mUsbCommander.lightLed(0);
        sleep(400);
    }
}
