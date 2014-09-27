package net.qiujuer.genius.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by QiuJu
 * on 2014/9/22.
 */
public class HashUtils {
    private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'};

    private static String convertToHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte aB : b) {
            sb.append(HEX_DIGITS[(aB & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[aB & 0x0f]);
        }
        return sb.toString();
    }

    /**
     * Get a String's HashCode
     *
     * @param str String
     * @return HashCode
     */
    public static String getStringMd5(String str) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        md5.update(str.getBytes());
        byte[] m = md5.digest();
        return convertToHexString(m);
    }

    /**
     * Get a File's HashCode
     *
     * @param file File
     * @return HashCode
     */
    public static String getFileMd5(File file) {
        InputStream in = null;
        byte[] buffer = new byte[1024];
        int numRead;
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        try {
            in = new FileInputStream(file);
            while ((numRead = in.read(buffer)) > 0) {
                md5.update(buffer, 0, numRead);
            }
            return convertToHexString(md5.digest());
        } catch (Exception e) {
            return null;
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}