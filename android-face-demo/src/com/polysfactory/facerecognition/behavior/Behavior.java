package com.polysfactory.facerecognition.behavior;

import android.util.Log;

import com.polysfactory.facerecognition.App;
import com.polysfactory.facerecognition.AudioCommander;
import com.polysfactory.facerecognition.EmotionManager;
import com.polysfactory.facerecognition.UsbCommander;
import com.polysfactory.facerecognition.behavior.BehaviorManager.BehaviorInfo;

/**
 * 振る舞いパターンの基底インターフェース<br>
 * @author $Author: horikawa.takahiro@gmail.com $
 * @version $Revision: 71 $
 */
public abstract class Behavior implements Runnable {

    UsbCommander mUsbCommander;

    AudioCommander mAudioCommander;

    EmotionManager mEmotionManager;

    BehaviorInfo mBehaviorInfo;

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public abstract void execute();

    @Override
    public void run() {
        if (!waitAndCheck()) {
            return;
        }
        mBehaviorInfo.isRunning = true;
        mBehaviorInfo.canInterrupt = true;
        execute();
        mBehaviorInfo.stopFlag = false;
        mBehaviorInfo.isRunning = false;
    }

    /**
     * ほかのスレッドの実行状況をチェックして自分が実行できるかをチェックする<br>
     * @return
     */
    boolean waitAndCheck() {
        if (mBehaviorInfo.isRunning) {
            if (mBehaviorInfo.canInterrupt) {
                mBehaviorInfo.stopFlag = true;
                for (int i = 0; i < 20; i++) {
                    sleep(100);
                    if (!mBehaviorInfo.isRunning) {
                        // スレッドが停止したら、自スレッドを再会
                        return true;
                    }
                }
                // 指定された秒数だけ待ってもほかのスレッドが停止しない場合、あきらめる
                Log.d(App.TAG, "give up waiting for another thread to die.");
                return false;
            } else {
                // ほかのスレッドが中断できない処理の場合、あきらめる
                Log.d(App.TAG, "another thread that can't be interrupted is running.");
                return false;
            }
        } else {
            // ほかのスレッドが実行されていない場合、自分は実行してOK
            return true;
        }
    }
}
