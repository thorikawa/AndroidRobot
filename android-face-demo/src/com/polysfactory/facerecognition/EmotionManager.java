package com.polysfactory.facerecognition;

/**
 * 感情コントローラ<br>
 * @author $Author$
 * @version $Revision$
 */
public class EmotionManager {

    private static final int THREASHOLD = 75;

    /**
     * 感情のENUM<br>
     * @author $Author$
     * @version $Revision$
     */
    public enum Emotion {
        ANGRY, HAPPY, SAD, NEAUTRAL
    }

    private int angry = 50;

    private int happy = 50;

    private int sad = 50;

    public void feelBad() {
        if (sad < 75) {
            sad += 10;
            angry += 5;
            happy -= 5;
        } else {
            angry += 10;
            sad += 3;
            happy -= 5;
        }
    }

    public void feelGood() {
        happy += 10;
        sad -= 5;
        angry -= 10;
    }

    public Emotion getEmotion() {
        if (angry > happy && angry > sad && angry >= THREASHOLD) {
            return Emotion.ANGRY;
        } else if (happy > angry && happy > sad && happy >= THREASHOLD) {
            return Emotion.HAPPY;
        } else if (sad > angry && sad > happy && sad >= THREASHOLD) {
            return Emotion.SAD;
        } else {
            return Emotion.NEAUTRAL;
        }
    }
}
