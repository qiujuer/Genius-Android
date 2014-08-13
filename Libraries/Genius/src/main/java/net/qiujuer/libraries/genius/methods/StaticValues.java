package net.qiujuer.libraries.genius.methods;

import android.content.Context;

/**
 * Created by Genius on 2014/8/13.
 * 静态值类
 */
public class StaticValues {
    private static Context CONTEXT;

    public static void setContext(Context context) {
        CONTEXT = context;
    }

    public static Context getContext() {
        return CONTEXT;
    }
}
