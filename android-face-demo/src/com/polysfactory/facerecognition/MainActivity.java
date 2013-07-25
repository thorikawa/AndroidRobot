package com.polysfactory.facerecognition;

import java.util.LinkedList;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.opencv.camera.NativePreviewer;
import com.opencv.camera.NativeProcessor;
import com.opencv.camera.NativeProcessor.PoolCallback;
import com.opencv.jni.image_pool;
import com.opencv.opengl.GL2CameraViewer;
import com.polysfactory.facerecognition.Brain.OnRebootCommandListener;

/**
 * Androidくんメインアクティビティ<br>
 * @author $Author: horikawa.takahiro@gmail.com $
 * @version $Revision: 69 $
 */
public class MainActivity extends Activity {

    private final int FOOBARABOUT = 0;

    UsbCommander mUsbCommander = null;

    Brain brain;

    boolean hasUsb = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        FrameLayout frame = new FrameLayout(this);

        // Create our Preview view and set it as the content of our activity.
        mPreview = new NativePreviewer(getApplication(), 300, 300);

        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.height = getWindowManager().getDefaultDisplay().getHeight();
        params.width = (int) (params.height * 4.0 / 2.88);

        LinearLayout vidlay = new LinearLayout(getApplication());

        vidlay.setGravity(Gravity.CENTER);
        vidlay.addView(mPreview, params);
        frame.addView(vidlay);

        // make the glview overlay ontop of video preview
        mPreview.setZOrderMediaOverlay(false);

        // set Auto Focus
        mPreview.postautofocus(0);

        glview = new GL2CameraViewer(getApplication(), false, 0, 0);
        glview.setZOrderMediaOverlay(true);
        glview.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        frame.addView(glview);

        setContentView(frame);

        if (hasUsb) {
            mUsbCommander = new UsbCommander(this);
        }
        brain = new Brain(this, mUsbCommander);
        brain.setOnRebootCommandListener(new OnRebootCommandListener() {
            @Override
            public void onRebootCommand() {
                reboot();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mUsbCommander != null) {
            mUsbCommander.unregisterReceiver();
        }
        brain.reset();
        super.onDestroy();
    }

    /*
     * Handle the capture button as follows...
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        switch (keyCode) {
        case KeyEvent.KEYCODE_CAMERA:
        case KeyEvent.KEYCODE_SPACE:
        case KeyEvent.KEYCODE_DPAD_CENTER:
            // capture button pressed here
            return true;

        default:
            return super.onKeyUp(keyCode, event);
        }

    }

    /*
     * Handle the capture button as follows... On some phones there is no capture button, only trackball
     */
    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            // capture button pressed
            return true;
        }
        return super.onTrackballEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(R.string.about_menu);
        menu.add(R.string.Reboot);
        return true;
    }

    private NativePreviewer mPreview;

    private GL2CameraViewer glview;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // example menu
        String title = item.getTitle().toString();
        if (title.equals(getString(R.string.about_menu))) {
            showDialog(FOOBARABOUT);
        } else if (title.equals(getString(R.string.Reboot))) {
            // ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            // am.restartPackage(getPackageName());
            reboot();
        }
        return true;
    }

    private void reboot() {
        finish();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // IMPORTANT
        // must tell the NativePreviewer of a pause
        // and the glview - so that they can release resources and start back up
        // properly
        // failing to do this will cause the application to crash with no
        // warning
        // on restart
        // clears the callback stack
        mPreview.onPause();

        glview.onPause();

        // USBアクセサリ関連
        if (mUsbCommander != null) {
            mUsbCommander.closeAccessory();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // resume the opengl viewer first
        glview.onResume();

        // add an initial callback stack to the preview on resume...
        // this one will just draw the frames to opengl
        LinkedList<NativeProcessor.PoolCallback> cbstack = new LinkedList<PoolCallback>();

        // SpamProcessor will be called first
        cbstack.add(new Processor());

        // then the same idx and pool will be passed to
        // the glview callback -
        // so operate on the image at idx, and modify, and then
        // it will be drawn in the glview
        // or remove this, and call glview manually in SpamProcessor
        // cbstack.add(glview.getDrawCallback());

        mPreview.addCallbackStack(cbstack);
        mPreview.onResume();

        if (mUsbCommander != null) {
            mUsbCommander.reopen();
        }
    }

    class Processor implements NativeProcessor.PoolCallback {

        @Override
        public void process(int idx, image_pool pool, long timestamp, NativeProcessor nativeProcessor) {

            // example of using the jni generated FoobarStruct;
            brain.process(idx, pool, timestamp);

            // these are what the glview.getDrawCallback() calls
            glview.drawMatToGL(idx, pool);
            glview.requestRender();
        }
    }

}
