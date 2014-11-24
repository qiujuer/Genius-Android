package net.qiujuer.genius;

import android.app.Application;

import net.qiujuer.genius.app.ToolKit;
import net.qiujuer.genius.command.Command;
import net.qiujuer.genius.util.Log;

/**
 * Created by QiuJu
 * on 2014/9/17.
 */
public final class Genius {
    private static Application application;

    public static Application getApplication() {
        return application;
    }

    public static void initialize(Application application) {
        Genius.application = application;
    }

    public static void dispose() {
        Command.dispose();
        Log.dispose();
        ToolKit.dispose();
        application = null;
    }
}
