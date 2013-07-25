package com.polysfactory.facerecognition;

import android.util.Log;

public class CommandUtils {

    public static int random(int max) {
        return (int) (Math.random() * max);
    }

    public static void randomLengthThinking(AudioCommander audioCommander) {
        audioCommander.ringThinkingSound(random(1), 1);
    }

    public static void randomEye(UsbCommander usbCommander) {
        usbCommander.lightLed(random(64));
    }

    public static void randomMove(UsbCommander usbCommander) {
        int i = random(6);
        switch (i) {
        case 0:
            usbCommander.forward();
            break;
        case 1:
            usbCommander.backward();
            break;
        case 2:
            usbCommander.spinTurnLeft();
            break;
        case 3:
            usbCommander.spinTurnRight();
            break;
        case 4:
            usbCommander.pivotTurnLeft();
            break;
        case 5:
            usbCommander.pivotTurnRight();
            break;
        default:
            break;
        }
    }

    public static void randomHand(UsbCommander usbCommander) {
        int i = (int) (Math.random() * 8);
        switch (i) {
        case 0:
            usbCommander.rotateLeftHand(63);
            break;
        case 1:
            usbCommander.rotateRightHand(63);
            break;
        case 2:
            usbCommander.rotateNeck(63);
            break;
        case 3:
            usbCommander.rotateLeftHand(0);
            break;
        case 4:
            usbCommander.rotateRightHand(0);
            break;
        case 5:
            usbCommander.rotateNeck(32);
            break;
        case 6:
            usbCommander.rotateNeck(0);
            break;
        default:
            // do nothing
            Log.d(App.TAG, "do nothing");
            break;
        }
    }

}
