package com.polysfactory.facerecognition.behavior;

import android.content.ContentUris;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;

import com.polysfactory.facerecognition.App;
import com.polysfactory.facerecognition.CommandUtils;

public class Dance extends Behavior {

    @Override
    public void execute() {
        mAudioCommander.playMusic(ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, 29));
        for (int i = 0; i < 40; i++) {
            CommandUtils.randomEye(mUsbCommander);
            sleep(100);
            CommandUtils.randomMove(mUsbCommander);
            sleep(100);
            CommandUtils.randomHand(mUsbCommander);
            sleep(400);
            if (mBehaviorInfo.stopFlag) {
                break;
            }
        }
        stop();
    }

    private void stop() {
        Log.d(App.TAG, "interrupted while dancing.");
        mAudioCommander.stopMusic();
        mUsbCommander.stop();
        sleep(100);
        mUsbCommander.lightLed(0);
        sleep(100);
    }
}
