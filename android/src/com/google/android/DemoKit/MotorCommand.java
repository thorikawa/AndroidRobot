package com.google.android.DemoKit;

/**
 * モーターを制御するコマンド群<br>
 * @author $Author: horikawa.takahiro@gmail.com $
 * @version $Revision: 69 $
 */
public class MotorCommand {
    public static final byte commandBase = (0x01) << 6;

    public static final byte motor1Stop = 0;

    // public static final byte motor1Forward = (0x01) << 2;
    public static final byte motor1Backward = (0x01) << 2;

    // public static final byte motor1Backward = (3) << 2;
    public static final byte motor1Forward = (3) << 2;

    public static final byte motor2Stop = 0;

    // public static final byte motor2Foreward = (0x01) << 4;
    public static final byte motor2Backward = (0x01) << 4;

    // public static final byte motor2Backward = (byte) ((3) << 4);
    public static final byte motor2Forward = (byte) ((3) << 4);

    private byte commandByte = 0;

    private byte motor1ActionTimeByte = 0;

    private byte motor2ActionTimeByte = 0;

    public void setCommandByte(byte flag) {
        commandByte = (byte) (flag | commandBase);
    }

    /**
     * コマンド全体のバイト列を返す<br>
     * @return
     */
    public byte[] toBytes() {
        byte[] r = new byte[3];
        r[0] = commandByte;
        r[1] = motor1ActionTimeByte;
        r[2] = motor2ActionTimeByte;
        System.out.println("====MotorCommand STT===");
        for (int i = 0; i < 3; i++) {
            System.out.println(r[i]);
        }
        System.out.println("====MotorCommand END===");
        return r;
    }
}