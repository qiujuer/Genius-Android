package net.qiujuer.genius.app;

import android.app.Activity;

/**
 * Created by QiuJu
 * on 2014/10/4.
 */
public class UiTool {

    /**
     * Synchronously
     * The child thread relative thread synchronization operation,
     * blocking the child thread,
     * thread for the main thread to complete
     *
     * @param activity Activity
     * @param ui       UiModel
     */
    public static void syncRunOnUiThread(Activity activity, final UiModel ui) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                synchronized (ui.object) {
                    ui.doUi();
                    try {
                        ui.object.notifyAll();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                ui.end = true;
            }
        });
        if (!ui.end) {
            synchronized (ui.object) {
                try {
                    ui.object.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ui.end = false;
            }
        }
    }

    /**
     * Asynchronously
     * The child thread asynchronous run relative to the main thread,
     * not blocking the child thread
     *
     * @param activity Activity
     * @param ui       UiModel
     */
    public static void asyncRunOnUiThread(Activity activity, final UiModel ui) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ui.doUi();
            }
        });
    }
}
