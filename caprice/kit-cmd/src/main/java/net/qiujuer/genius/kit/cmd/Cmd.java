package net.qiujuer.genius.kit.cmd;

import android.content.Context;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by qiujuer
 * on 16/4/17.
 */
public class Cmd {
    // This is same status to Net request
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

    static final String PING = "ping";
    static final String PING_FROM = "from";
    static final String PING_PAREN_THESE_OPEN = "(";
    static final String PING_PAREN_THESE_CLOSE = ")";
    static final String PING_EXCEED = "exceed";
    static final String PING_STATISTICS = "statistics";
    static final String PING_TRANSMIT = "packets transmitted";
    static final String PING_RECEIVED = "received";
    static final String PING_ERRORS = "errors";
    static final String PING_LOSS = "packet loss";
    static final String PING_UNREACHABLE = "100%";
    static final String PING_RTT = "rtt";
    static final String PING_BREAK_LINE = "\n";
    static final String PING_RATE = "%";
    static final String PING_COMMA = ",";
    static final String PING_EQUAL = "=";
    static final String PING_SLASH = "/";

    // This is to be used service init
    private static Context CONTEXT;

    /**
     * Get the Context to run service
     *
     * @return Context
     */
    static Context getContext() {
        return CONTEXT;
    }

    /**
     * Can close the in or out thread
     *
     * @param closeable Closeable
     */
    static void closeIO(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Init the Cmd
     * You should call the method before use: {@link Ping } or {@link TraceRoute}
     *
     * @param context Context
     */
    public static void init(Context context) {
        CONTEXT = context;
    }

}
