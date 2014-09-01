package net.qiujuer.genius.utils;

/**
 * Created by Genius on 2014/8/13.
 * 常用工具方法
 */
public class ToolUtils {
    public static void sleepIgnoreInterrupt(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
