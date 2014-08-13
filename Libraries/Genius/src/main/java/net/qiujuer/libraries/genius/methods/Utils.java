package net.qiujuer.libraries.genius.methods;

/**
 * Created by Genius on 2014/8/13.
 */
public class Utils {
    public static void sleepIgnoreInterrupt(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
