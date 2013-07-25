package com.google.android.DemoKit;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;

public class Top extends Activity implements Runnable {
    private static final String TAG = "DemoKit";

    private static final String ACTION_USB_PERMISSION = "com.google.android.DemoKit.action.USB_PERMISSION";

    UsbAccessory mAccessory;

    int servo1Level = 0;

    UsbCommander usbCommander;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        usbCommander = new UsbCommander(this);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);

        if (getLastNonConfigurationInstance() != null) {
            mAccessory = (UsbAccessory) getLastNonConfigurationInstance();
            usbCommander.openAccessory(mAccessory);
        }

        setContentView(R.layout.top);
        Button stopMotor1Button = (Button) findViewById(R.id.StopMotor1);
        Button forwardMotor1Button = (Button) findViewById(R.id.ForwardMotor1);
        Button backwardMotor1Button = (Button) findViewById(R.id.BackwardMotor1);
        Button servo1 = (Button) findViewById(R.id.Servo1);
        Button servo2 = (Button) findViewById(R.id.Servo2);
        Button servo3 = (Button) findViewById(R.id.Servo3);
        stopMotor1Button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                usbCommander.stop();
            }
        });
        forwardMotor1Button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                usbCommander.forward();
            }
        });
        backwardMotor1Button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                usbCommander.backward();
            }
        });
        servo1.setOnClickListener(new OnClickListener() {
            int n = 0;

            @Override
            public void onClick(View arg0) {
                if ((n++) % 2 == 1) {
                    usbCommander.rotateLeftHand(0);
                } else {
                    usbCommander.rotateLeftHand(63);
                }
            }
        });
        servo2.setOnClickListener(new OnClickListener() {
            int n = 0;

            @Override
            public void onClick(View arg0) {
                if ((n++) % 2 == 1) {
                    usbCommander.rotateRightHand(0);
                } else {
                    usbCommander.rotateRightHand(63);
                }
            }
        });
        servo3.setOnClickListener(new OnClickListener() {
            int n = 0;

            @Override
            public void onClick(View arg0) {
                if ((n++) % 2 == 1) {
                    usbCommander.rotateNeck(0);
                } else {
                    usbCommander.rotateNeck(63);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        usbCommander.unregisterReceiver();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        usbCommander.closeAccessory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        usbCommander.reopen();
    }

    protected void enableControls(boolean enable) {
    }

    public void run() {
        // TODO アクセサリからのデータの読み取り
    }

}
