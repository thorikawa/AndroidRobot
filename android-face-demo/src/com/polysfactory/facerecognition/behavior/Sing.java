package com.polysfactory.facerecognition.behavior;

import android.net.Uri;
import android.util.Log;

import com.polysfactory.facerecognition.App;
import com.polysfactory.facerecognition.CommandUtils;
import com.polysfactory.facerecognition.R;

public class Sing extends Behavior {

    @Override
    public void execute() {
        Log.d(App.TAG, "sing!");
        int n = (int) (2 * Math.random());

        switch (n) {
        case 0:
            mAudioCommander.playMusic(
                    Uri.parse("android.resource://com.polysfactory.facerecognition/" + R.raw.droidohatsuzuku)
                    );
            for (int i = 0; i < 4; i++) {
                CommandUtils.randomEye(mUsbCommander);
                sleep(100);
                mUsbCommander.forward();
                sleep(1000);
                mUsbCommander.spinTurnRight();
                sleep(500);
            }
            mUsbCommander.stop();
            sleep(100);
            mUsbCommander.lightLed(0);
            sleep(100);
            break;
        case 1:
            mAudioCommander.playMusic(
                    Uri.parse("android.resource://com.polysfactory.facerecognition/" + R.raw.polys_light_normal)
                    );
            for (int i = 0; i < 13; i++) {
                CommandUtils.randomEye(mUsbCommander);
                sleep(700);
            }
            mUsbCommander.lightLed(0);
            sleep(100);
            break;
        default:
            break;
        }
    }

}
