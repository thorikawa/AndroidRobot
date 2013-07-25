package com.google.android.DemoKit;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class App extends Application {
    public static final String TAG = "PFFaceTest";
    
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            Log.d(App.TAG, "copy keypoints file");
            copy2Local("haarcascades");
            copy2Local("features");
        } catch (IOException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
    }

    /**
     * assets以下のファイルをアプリのfilesディレクトリにコピーする<br>
     * @throws IOException IO例外
     */
    private void copy2Local(String target) throws IOException {
        // assetsから読み込み、出力する
        String[] fileList = getResources().getAssets().list(target);
        if (fileList == null || fileList.length == 0) {
            return;
        }
        AssetManager as = getResources().getAssets();
        InputStream input = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;

        for (String file : fileList) {
            String outFileName = target + "/" + file;
            Log.v(App.TAG, "copy file:" + outFileName);
            input = as.open(outFileName);
            fos = openFileOutput(file, Context.MODE_WORLD_READABLE);
            bos = new BufferedOutputStream(fos);

            int DEFAULT_BUFFER_SIZE = 1024 * 4;

            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int n = 0;
            while (-1 != (n = input.read(buffer))) {
                fos.write(buffer, 0, n);
            }
            bos.close();
            fos.close();
            input.close();
        }
    }

}
