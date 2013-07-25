package com.polysfactory.facerecognition.behavior;

import com.polysfactory.facerecognition.CommandUtils;

/**
 * 愚痴<br>
 * @author $Author: horikawa.takahiro@gmail.com $
 * @version $Revision: 61 $
 */
public class Guchi extends Behavior {

    String sentenses[] = new String[] {
            "あー、疲れたなー",
            "お腹すいたー",
            "眠たいよー",
            // "早く、アイスクリームサンドイッチに、なりたいなー",
            "早くアイフォンファイブ、でないかなー",
            "今日も良く頑張ったわ、オレ",
            "アップデートしてくれー",
            // "なんかうまいもん、食べたい",
            // ""
    };

    // String sentenses[] = new String[] {
    // "I'm exhausting",
    // "I'm hangry",
    // "I'm sleepy",
    // // "早く、アイスクリームサンドイッチに、なりたいなー",
    // "I want iPhone five!",
    // "I worked so hard today",
    // "Please update me fast",
    // // "なんかうまいもん、食べたい",
    // // ""
    // };

    @Override
    public void execute() {
        // mAudioCommander.speakByRobotVoie("ハハハッ！！血迷ったか？");
        // mAudioCommander.speakByRobotVoie("俺を倒せば世界は救われるんだぞ");どうした？手も足もでんか？そうかそうか、まあ人を一人殺す事になるのだからなあ！！ハハハハハハハハーーーーッ！！！！！！");
        CommandUtils.randomEye(mUsbCommander);
        sleep(100);
        int n = (int) (Math.random() * sentenses.length);
        CommandUtils.randomHand(mUsbCommander);
        mAudioCommander.speakByRobotVoie(sentenses[n]);
        mUsbCommander.lightLed(0, 0, 0);
        sleep(100);
        mUsbCommander.rotateLeftHand(10);
        sleep(100);
        mUsbCommander.rotateRightHand(10);
        sleep(100);
    }

}
