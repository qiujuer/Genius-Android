package net.qiujuer.genius.util;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created by QiuJu
 * on 2014/8/13.
 * This is a util tools kit
 */
public final class Tools {
    /**
     * Sleep time
     * Don't throw an InterruptedException exception
     *
     * @param time long time
     */
    public static void sleepIgnoreInterrupt(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Copy file to file
     *
     * @param source Source File
     * @param target Target File
     * @return isCopy ok
     */
    public static boolean copyFile(File source, File target) {
        boolean bFlag = false;
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            if (!target.exists()) {
                boolean createSuccess = target.createNewFile();
                if (!createSuccess) {
                    return false;
                }
            }
            in = new FileInputStream(source);
            out = new FileOutputStream(target);
            byte[] buffer = new byte[8 * 1024];
            int count;
            while ((count = in.read(buffer)) != -1) {
                out.write(buffer, 0, count);
            }
            bFlag = true;
        } catch (Exception e) {
            e.printStackTrace();
            bFlag = false;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bFlag;
    }

    /**
     * Equipment is started for the first time the generated number
     * Are potential "9774d56d682e549c"
     *
     * @param context Context
     * @return Number
     */
    public static String getAndroidId(Context context) {
        return android.provider.Settings.System.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
    }

    /**
     * This device's SN
     *
     * @return SerialNumber
     */
    public static String getSerialNumber() {
        String serialNumber = android.os.Build.SERIAL;
        if ((serialNumber == null || serialNumber.length() == 0 || serialNumber.contains("unknown"))) {
            String[] keys = new String[]{"ro.boot.serialno", "ro.serialno"};
            for (String key : keys) {
                try {
                    Method systemProperties_get = Class.forName("android.os.SystemProperties").getMethod("get", String.class);
                    serialNumber = (String) systemProperties_get.invoke(null, key);
                    if (serialNumber != null && serialNumber.length() > 0 && !serialNumber.contains("unknown"))
                        break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return serialNumber;
    }
}
