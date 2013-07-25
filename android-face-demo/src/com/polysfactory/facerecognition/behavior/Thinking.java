package com.polysfactory.facerecognition.behavior;

import com.polysfactory.facerecognition.CommandUtils;

public class Thinking extends Behavior {

    @Override
    public void execute() {
        CommandUtils.randomLengthThinking(mAudioCommander);
        CommandUtils.randomEye(mUsbCommander);
        sleep(100);
        CommandUtils.randomHand(mUsbCommander);
        sleep(1000);
        CommandUtils.randomEye(mUsbCommander);
        sleep(100);
        CommandUtils.randomHand(mUsbCommander);
        sleep(1000);
        CommandUtils.randomEye(mUsbCommander);
        sleep(100);
        mUsbCommander.lightLed(0);
    }

}
