package com.polysfactory.facerecognition;

import java.io.IOException;
import java.util.Locale;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

import com.polysfactory.facerecognition.util.IOUtils;
import com.polysfactory.robotaudio.jni.RobotAudio;

/**
 * 音声関連の命令を実行するクラス<br>
 * @author $Author: horikawa.takahiro@gmail.com $
 * @version $Revision: 69 $
 */
public class AudioCommander {
    /** ロボット声加工機 */
    RobotAudio robotAudio = new RobotAudio("/sdcard/poly.wav", "/sdcard/robot.wav");

    /** メディアプレイヤー */
    MediaPlayer mp = null;

    /** 音声 */
    TextToSpeech tts;

    Context mContext;

    SoundPool soundPool;

    private int thinkingSoundId;

    private int bootSoundId;

    public AudioCommander(Context context) {
        mContext = context;
        // setup TextToSpeech object
        tts = new TextToSpeech(context, new OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // ロケールの指定
                    // Locale locale = Locale.ENGLISH;
                    Locale locale = Locale.JAPANESE;
                    if (tts.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE) {
                        tts.setLanguage(locale);
                        tts.setPitch(0.5F);
                        tts.setSpeechRate(0.7F);
                        // tts.setSpeechRate(1.0F);
                    } else {
                        Log.e(App.TAG, "Error SetLocale");
                    }
                }
            }
        });
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool arg0, int soundId, int status) {
                if (soundId == bootSoundId) {
                    bootSound();
                }
            }
        });
        // thinkingSoundId = soundPool.load(context, R.raw.thinking, 1);
        thinkingSoundId = soundPool.load(context, R.raw.thinking2, 1);
        bootSoundId = soundPool.load(context, R.raw.startup1, 1);
    }

    /**
     * ロボット思考中の音声をならす<br>
     * @param loop ループ回数（-1の場合は無限にループ、0の場合はループしない）
     * @param rate 再生速度（0.5〜2.0：0.5倍から2倍の速度まで設定できる）
     */
    public void ringThinkingSound(int loop, float rate) {
        Log.d(App.TAG, "ring thinking sound:" + loop);
        soundPool.play(thinkingSoundId, 0.5F, 0.5F, 1, loop, rate);
    }

    public void test() {
        String target[] = new String[] {
                "あーーーーーーーーーーーー",
                "いーーーーーーーーーーーー",
                "うーーーーーーーーーーーー",
                "えーーーーーーーーーーーー",
                "おーーーーーーーーーーーー"
        };
        int counter = 0;
        for (String text : target) {
            tts.synthesizeToFile(text, null, "/sdcard/poly.wav");
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            robotAudio.pitchShift(10);
            try {
                // IOUtils.copyTransfer("/sdcard/robot.wav", "/sdcard/" + text + ".wav");
                IOUtils.copyTransfer("/sdcard/robot.wav", "/sdcard/" + counter + ".wav");
            } catch (IOException e) {
                e.printStackTrace();
            }
            counter++;
        }
    }

    /**
     * ロボット声で喋る<br>
     * @param text 喋らせる内容
     */
    public void speakByRobotVoie(String text) {
        tts.synthesizeToFile(text, null, "/sdcard/poly.wav");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            // TODO 自動生成された catch ブロック
            e1.printStackTrace();
        }
        robotAudio.pitchShift(10);
        if (mp != null) {
            mp.release();
            mp = null;
        }
        mp = new MediaPlayer();
        try {
            mp.setDataSource("/sdcard/robot.wav");
            mp.prepare();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.start();
    }

    /**
     * 音をならす<br>
     * @param audioUri
     */
    public void playMusic(Uri audioUri) {
        if (mp != null) {
            mp.release();
            mp = null;
        }
        mp = new MediaPlayer();
        try {
            mp.setDataSource(mContext, audioUri);
            mp.prepare();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        mp.start();
    }

    /**
     * 再生を停止する<br>
     */
    public void stopMusic() {
        if (mp != null) {
            mp.release();
            mp = null;
        }
    }

    /**
     * 起動音をならす<br>
     */
    public void bootSound() {
        soundPool.play(bootSoundId, 1.0F, 1.0F, 1, 0, 0);
    }
}
