package net.qiujuer.genius.util;

import android.content.Context;

/**
 * Created by Genius on 2014/8/13.
 * 全局静态值类
 */
public class GlobalValue {
    private static Context mApplicationContext;

    public static Context getApplicationContext() {
        return mApplicationContext;
    }

    public static void setApplicationContext(Context context) {
        mApplicationContext = context.getApplicationContext();
    }
}
