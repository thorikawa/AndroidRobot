package com.polysfactory.facerecognition.behavior;

import android.util.Log;

import com.polysfactory.facerecognition.App;
import com.polysfactory.facerecognition.CommandUtils;

/**
 * あいさつの振る舞い<br>
 * @author $Author: horikawa.takahiro@gmail.com $
 * @version $Revision: 61 $
 */
public class GuruGuru extends Behavior {

    @Override
    public void execute() {
        Log.d(App.TAG, "GuruGuru::action");
        mAudioCommander.speakByRobotVoie("ぴーひゃららー、ぴーひゃららー");
        CommandUtils.randomEye(mUsbCommander);
        sleep(100);
        mUsbCommander.spinTurnLeft();
        sleep(2500);
        CommandUtils.randomEye(mUsbCommander);
        sleep(100);
        mUsbCommander.spinTurnRight();
        sleep(2500);
        mUsbCommander.stop();
        sleep(100);
        mUsbCommander.lightLed(0, 0, 0);
    }

}
