package com.google.android.DemoKit;

import junit.framework.Assert;

/**
 * サーボを制御するコマンド群<br>
 * @author $Author: horikawa.takahiro@gmail.com $
 * @version $Revision: 61 $
 */
public class LedCommand {

    private static final int NBYTE = 3;

    public static final byte commandBase = (byte) (3 << 6);

    private byte commandByte = 0;

    private int[] ledColor = new int[2];

    /**
     * コンストラクタ<br>
     */
    public LedCommand() {
        for (int i = 0; i < 2; i++) {
            ledColor[i] = -1;
        }
    }

    /**
     * 指定されたサーボの回転角をセットする<br>
     * @param ledId
     * @param scale
     */
    public void setLedScale(int ledId, int scale) {
        Assert.assertEquals(true, scale < 64);
        ledColor[ledId] = scale;
    }

    public void setLedScale(int ledId, int red, int green, int blue) {
        Assert.assertEquals(true, red < 4 && green < 4 && blue < 4);
        int scale = (red) + (green << 2) + (blue << 4);
        setLedScale(ledId, scale);
    }

    /**
     * コマンド全体のバイト列を返す<br>
     * @return
     */
    public byte[] toBytes() {
        byte[] r = new byte[NBYTE];
        r[0] = commandBase;
        for (int i = 0; i < 2; i++) {
            if (ledColor[i] >= 0) {
                r[0] |= (1 << i);
                r[i + 1] = (byte) (ledColor[i]);
            }
        }

        System.out.println("====LedCommand STT===");
        for (int i = 0; i < NBYTE; i++) {
            System.out.println(r[i]);
        }
        System.out.println("====LedCommand END===");
        return r;
    }
}