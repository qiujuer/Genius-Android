package net.qiujuer.genius.kit;

import android.app.Application;

import net.qiujuer.genius.kit.reflect.Reflect;

/**
 * Created by qiujuer
 * on 16/4/17.
 */
public class Kit {
    public static Application getApplication() {
        return Reflect.with("android.app.ActivityThread")
                .call("currentApplication")
                .get();
    }
}
