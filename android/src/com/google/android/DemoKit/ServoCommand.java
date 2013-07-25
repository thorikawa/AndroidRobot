package com.google.android.DemoKit;

import junit.framework.Assert;

/**
 * サーボを制御するコマンド群<br>
 * @author $Author: horikawa.takahiro@gmail.com $
 * @version $Revision: 69 $
 */
public class ServoCommand {
    public static final byte commandBase = (byte) (2 << 6);

    private byte commandByte = 0;

    private int[] servoDegree = new int[3];

    /**
     * コンストラクタ<br>
     */
    public ServoCommand() {
        for (int i = 0; i < 3; i++) {
            servoDegree[i] = -1;
        }
    }

    /**
     * 指定されたサーボの回転角をセットする<br>
     * @param servoId
     * @param degree
     */
    public void setServoDegree(int servoId, int degree) {
        Assert.assertEquals(true, degree < 64);
        if (servoId == 1) {
            degree = 63 - degree;
        }
        servoDegree[servoId] = degree;
    }

    /**
     * コマンド全体のバイト列を返す<br>
     * @return
     */
    public byte[] toBytes() {
        byte[] r = new byte[4];
        r[0] = commandBase;
        for (int i = 0; i < 3; i++) {
            if (servoDegree[i] >= 0) {
                r[0] |= (1 << i);
                r[i + 1] = (byte) (servoDegree[i]);
            }
        }

        System.out.println("====ServoCommand STT===");
        for (int i = 0; i < 4; i++) {
            System.out.println(r[i]);
        }
        System.out.println("====ServoCommand END===");
        return r;
    }
}