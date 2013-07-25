package com.polysfactory.facerecognition.behavior;

import com.polysfactory.facerecognition.CommandUtils;

public class RoundWalk extends Behavior {

    @Override
    public void execute() {
        for (int i = 0; i < 4; i++) {
            CommandUtils.randomEye(mUsbCommander);
            sleep(100);
            mUsbCommander.forward();
            sleep(2000);
            mUsbCommander.spinTurnRight();
            sleep(1000);
        }
        mUsbCommander.stop();
        sleep(100);
        mUsbCommander.lightLed(0);
        sleep(100);
    }

}
