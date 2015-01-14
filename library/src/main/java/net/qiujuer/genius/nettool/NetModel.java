/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 09/20/2014
 * Changed 01/13/2015
 * Version 1.0.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.qiujuer.genius.nettool;

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

    protected int mError = SUCCEED;

    public abstract void start();

    public abstract void cancel();

    public int getError() {
        return mError;
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
}
