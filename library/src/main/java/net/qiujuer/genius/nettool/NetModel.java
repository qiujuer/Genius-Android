package net.qiujuer.genius.nettool;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by QiuJu
 * on 2014/9/20.
 */
public abstract class NetModel {
    public static final int SUCCEED = 0;
    public static final int UNKNOWN_ERROR = 1;
    public static final int TCP_LINK_ERROR = 2;
    public static final int UNKNOWN_HOST_ERROR = 3;
    public static final int NETWORK_FAULT_ERROR = 4;
    public static final int NETWORK_SOCKET_ERROR = 5;
    public static final int NETWORK_IO_ERROR = 6;
    public static final int MALFORMED_URL_ERROR = 7;
    public static final int HTTP_CODE_ERROR = 7;
    public static final int SERVICE_NOT_AVAILABLE = 8;
    public static final int DOWNLOAD_ERROR = 9;
    public static final int ICMP_ECHO_FAIL_ERROR = 10;
    public static final int HOST_UNREACHABLE_ERROR = 11;
    public static final int DROP_DATA_ERROR = 12;


    protected static final String PING = "ping";
    protected static final String PING_FROM = "from";
    protected static final String PING_PAREN_THESE_OPEN = "(";
    protected static final String PING_PAREN_THESE_CLOSE = ")";
    protected static final String PING_EXCEED = "exceed";
    protected static final String PING_STATISTICS = "statistics";
    protected static final String PING_TRANSMIT = "packets transmitted";
    protected static final String PING_RECEIVED = "received";
    protected static final String PING_ERRORS = "errors";
    protected static final String PING_LOSS = "packet loss";
    protected static final String PING_UNREACHABLE = "100%";
    protected static final String PING_RTT = "rtt";
    protected static final String PING_BREAK_LINE = "\n";
    protected static final String PING_RATE = "%";
    protected static final String PING_COMMA = ",";
    protected static final String PING_EQUAL = "=";
    protected static final String PING_SLASH = "/";

    protected int error = SUCCEED;

    public abstract void start();

    public abstract void cancel();

    public int getError() {
        return error;
    }

    protected static byte[] convertIpToByte(String ip) {
        String str[] = ip.split("\\.");
        byte[] bIp = new byte[str.length];
        try {
            for (int i = 0, len = str.length; i < len; i++) {
                bIp[i] = (byte) (Integer.parseInt(str[i], 10));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return bIp;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    protected static void closeInputStream(InputStream in) {
        if (in != null) {
            /*
            try {
                byte[] buffer = new byte[32];
                while (in.read(buffer) > 0) ;
            } catch (Exception e) {
                e.printStackTrace();
            }
            */
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
