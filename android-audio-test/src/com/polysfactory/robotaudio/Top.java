package com.polysfactory.robotaudio;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.polysfactory.robotaudio.jni.RobotAudio;

public class Top extends Activity {
    /** Called when the activity is first created. */

    private static final String TAG = "PFSpeechTest";

    MediaPlayer mp;

    EditText et;

    TextToSpeech tts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button button1 = (Button) findViewById(R.id.Button1);
        et = (EditText) findViewById(R.id.pitchText);
        tts = new TextToSpeech(this, new OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // ロケールの指定
                    // Locale locale = Locale.ENGLISH;
                    Locale locale = Locale.JAPANESE;
                    if (tts.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE) {
                        tts.setLanguage(locale);
                        // tts.setPitch(0.5F);
                        // tts.setSpeechRate(0.7F);
                    } else {
                        Log.e(TAG, "Error SetLocale");
                    }
                }
            }
        });
        tts.setOnUtteranceCompletedListener(new OnUtteranceCompletedListener() {
            @Override
            public void onUtteranceCompleted(String utteranceId) {
                Log.d(TAG, "onUtterance");
            }
        });
        button1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = et.getText().toString();
                Log.d(TAG, "text=" + text);
                HashMap<String, String> myHashRender = new HashMap<String, String>();
                myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "abc");
                tts.synthesizeToFile(text, myHashRender, "/sdcard/poly.wav");
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        Log.d(TAG, "convert start");
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            // TODO 自動生成された catch ブロック
                            e.printStackTrace();
                        }
                        RobotAudio roboAudio = new RobotAudio();
                        roboAudio.pitchShift(5.0F);
                        Log.d(TAG, "speech start");
                        playAudio("/sdcard/robot.wav");
                    }
                };
                t.start();
            }
        });
    }

    void playAudio(String filePath) {
        if (mp != null) {
            mp.release();
            mp = null;
        }
        mp = new MediaPlayer();
        try {
            mp.setDataSource(filePath);
            mp.prepare();
            mp.start();
        } catch (IllegalArgumentException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        } catch (IOException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
    }
}
