package com.polysfactory.contactphoto.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * ログのラッパークラス
 * @author $Author: horikawa.takahiro@gmail.com $
 * @version $Revision: 52 $
 */
public final class Log {

    private static final String TAG = "ContactRandomImage";

    /**
     * 新しいインスタンスを生成できないようにします。
     */
    private Log() {
        super();
    }

    /**
     * 詳細ログを出力します。
     * @param context Context
     * @param msg メッセージ
     */
    public static void v(Context context, String msg) {
        if (isDebugable(context)) {
            android.util.Log.v(TAG, msg);
        }
    }

    /**
     * 詳細ログを出力します。
     * @param context Context
     * @param msg メッセージ
     * @param tr 例外
     */
    public static void v(Context context, String msg, Throwable tr) {
        if (isDebugable(context)) {
            android.util.Log.v(TAG, msg, tr);
        }
    }

    /**
     * デバックログを出力します。
     * @param context Context
     * @param msg メッセージ
     */
    public static void d(Context context, String msg) {
        if (isDebugable(context)) {
            android.util.Log.d(TAG, msg);
        }
    }

    /**
     * デバックログを出力します。
     * @param context Context
     * @param msg メッセージ
     * @param tr 例外
     */
    public static void d(Context context, String msg, Throwable tr) {
        if (isDebugable(context)) {
            android.util.Log.d(TAG, msg, tr);
        }
    }

    /**
     * 情報ログを出力します。
     * @param context Context
     * @param msg メッセージ
     */
    public static void i(Context context, String msg) {
        if (isDebugable(context)) {
            android.util.Log.i(TAG, msg);
        }
    }

    /**
     * 情報ログを出力します。
     * @param context Context
     * @param msg メッセージ
     * @param tr 例外
     */
    public static void i(Context context, String msg, Throwable tr) {
        if (isDebugable(context)) {
            android.util.Log.i(TAG, msg, tr);
        }
    }

    /**
     * 警告ログを出力します。
     * @param context Context
     * @param msg メッセージ
     */
    public static void w(Context context, String msg) {
        if (isDebugable(context)) {
            android.util.Log.w(TAG, msg);
        }
    }

    /**
     * 警告ログを出力します。
     * @param context Context
     * @param msg メッセージ
     * @param tr 例外
     */
    public static void w(Context context, String msg, Throwable tr) {
        if (isDebugable(context)) {
            android.util.Log.w(TAG, msg, tr);
        }
    }

    /**
     * 警告ログを出力します。
     * @param context Context
     * @param tr 例外
     */
    public static void w(Context context, Throwable tr) {
        if (isDebugable(context)) {
            android.util.Log.w(TAG, tr);
        }
    }

    /**
     * エラーログを出力します。
     * @param context Context
     * @param msg メッセージ
     */
    public static void e(Context context, String msg) {
        if (isDebugable(context)) {
            android.util.Log.e(TAG, msg);
        }
    }

    /**
     * エラーログを出力します。
     * @param context Context
     * @param msg メッセージ
     * @param tr 例外
     */
    public static void e(Context context, String msg, Throwable tr) {
        if (isDebugable(context)) {
            android.util.Log.e(TAG, msg, tr);
        }
    }

    /**
     * スタックトレースの文字列を取得します。
     * @param tr 例外
     * @return スタックトレースを文字列にして返します。
     */
    public static String getStackTraceString(Throwable tr) {
        return android.util.Log.getStackTraceString(tr);
    }

    /**
     * 標準出力にログを出力します。
     * @param context Context
     * @param priority 優先度
     * @param msg メッセージ
     */
    public static void println(Context context, int priority, String msg) {
        if (isDebugable(context)) {
            android.util.Log.println(priority, TAG, msg);
        }
    }

    /**
     * デバックモードか判定します。
     * @param context Context
     * @return デバックモードの場合はtrueを返します。
     */
    public static boolean isDebugable(Context context) {
        PackageManager manager = context.getPackageManager();
        ApplicationInfo appInfo = null;
        try {
            appInfo = manager.getApplicationInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            return false;
        }
        if (appInfo != null && (appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) == ApplicationInfo.FLAG_DEBUGGABLE) {
            return true;
        } else {
            return false;
        }
    }
}
