package com.polysfactory.facerecognition.behavior;

import java.util.Vector;

import com.polysfactory.facerecognition.AudioCommander;
import com.polysfactory.facerecognition.EmotionManager;
import com.polysfactory.facerecognition.UsbCommander;

/**
 * ロボットの振る舞いパターンを管理するクラス<br>
 * @author $Author: horikawa.takahiro@gmail.com $
 * @version $Revision: 70 $
 */
public class BehaviorManager {
    private Vector<Behavior> behaviorVector;

    private Behavior currentBehavior;

    private GreetToPersonBehavior greetToPersonBehavior;

    private Dance dance;

    private TalkBack talkBack;

    private Apologize apologize;

    private Shy shy;

    private Greeting greeting;

    private BehaviorInfo behaviorInfo;

    public class BehaviorInfo {
        /** 何らかのスレッドが実行中であることを示すフラグ */
        boolean isRunning = false;

        /** スレッドを終了させるためのフラグ */
        boolean stopFlag = false;

        /** ほかの振る舞いによって中断される状態かどうか */
        boolean canInterrupt = false;
    }

    public BehaviorManager(UsbCommander usbCommander, AudioCommander audioCommander, EmotionManager emotionManager) {
        behaviorInfo = new BehaviorInfo();
        // ランダムに能動的に行う振る舞いはVectorに突っ込む
        behaviorVector = new Vector<Behavior>();
        // behaviorVector.add(new Greeting());
        behaviorVector.add(new UroUro());
        behaviorVector.add(new KyoroKyoro());
        // behaviorVector.add(new Wondering());
        // behaviorVector.add(new Thinking());
        behaviorVector.add(new Guchi());
        behaviorVector.add(new GuruGuru());
        // behaviorVector.add(new Thinking());
        behaviorVector.add(new Sing());
        behaviorVector.add(new BataBata());

        // 外的要因によって受動的に行う振る舞いは個別のオブジェクトに持つ
        greetToPersonBehavior = new GreetToPersonBehavior();
        greetToPersonBehavior.mUsbCommander = usbCommander;
        greetToPersonBehavior.mAudioCommander = audioCommander;
        greetToPersonBehavior.mEmotionManager = emotionManager;
        greetToPersonBehavior.mBehaviorInfo = behaviorInfo;

        dance = new Dance();
        dance.mUsbCommander = usbCommander;
        dance.mAudioCommander = audioCommander;
        dance.mEmotionManager = emotionManager;
        dance.mBehaviorInfo = behaviorInfo;

        talkBack = new TalkBack();
        talkBack.mUsbCommander = usbCommander;
        talkBack.mAudioCommander = audioCommander;
        talkBack.mEmotionManager = emotionManager;
        talkBack.mBehaviorInfo = behaviorInfo;

        apologize = new Apologize();
        apologize.mUsbCommander = usbCommander;
        apologize.mAudioCommander = audioCommander;
        apologize.mEmotionManager = emotionManager;
        apologize.mBehaviorInfo = behaviorInfo;

        shy = new Shy();
        shy.mUsbCommander = usbCommander;
        shy.mAudioCommander = audioCommander;
        shy.mEmotionManager = emotionManager;
        shy.mBehaviorInfo = behaviorInfo;

        greeting = new Greeting();
        greeting.mUsbCommander = usbCommander;
        greeting.mAudioCommander = audioCommander;
        greeting.mEmotionManager = emotionManager;
        greeting.mBehaviorInfo = behaviorInfo;

        for (Behavior b : behaviorVector) {
            b.mUsbCommander = usbCommander;
            b.mAudioCommander = audioCommander;
            b.mEmotionManager = emotionManager;
            b.mBehaviorInfo = behaviorInfo;
        }
    }

    public void greetToPerson(String name) {
        greetToPersonBehavior.name = name;
        new Thread(greetToPersonBehavior).start();
    }

    public void dance() {
        new Thread(dance).start();
    }

    private Behavior getNewBehavior() {
        int n = (int) (Math.random() * behaviorVector.size());
        return behaviorVector.get(n);
    }

    public void talkBack() {
        new Thread(talkBack).start();
    }

    public void apologize() {
        new Thread(apologize).start();
    }

    public void feelShy() {
        new Thread(shy).start();
    }

    public void greet() {
        new Thread(greeting).start();
    }

    public void next() {
        currentBehavior = getNewBehavior();
        new Thread(currentBehavior).start();
    }

    public void stop() {
        behaviorInfo.stopFlag = true;
    }
}
