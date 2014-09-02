package net.qiujuer.genius.util;

import android.content.Context;

/**
 * Created by Genius on 2014/8/13.
 * 全局静态值类
 */
public class GlobalValue {
    private static Context CONTEXT;

    public static Context getContext() {
        return CONTEXT;
    }

    public static void setContext(Context context) {
        CONTEXT = context.getApplicationContext();
    }
}
