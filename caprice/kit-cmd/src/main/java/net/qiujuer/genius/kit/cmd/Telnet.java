/*
 * Copyright (C) 2014-2016 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 12/25/2014
 * Changed 04/17/2016
 * Version 2.0.0
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
package net.qiujuer.genius.kit.cmd;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Telnet class extends {@link AbsNet}
 * Telnet ip port
 */
public class Telnet extends AbsNet {
    private static final int TIME_OUT = 3000;
    private String mHost;
    private int mPort;
    private long mDelay;
    private boolean isConnected;

    /**
     * Telnet service
     *
     * @param host Service host
     * @param port Service port
     */
    public Telnet(String host, int port) {
        this.mHost = host;
        this.mPort = port;
    }

    @Override
    public void start() {
        Socket socket = null;
        try {
            Long startTime = System.currentTimeMillis();
            socket = new Socket();
            try {
                socket.setSoTimeout(TIME_OUT);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            socket.connect(new InetSocketAddress(mHost, mPort), TIME_OUT);
            if (isConnected = socket.isConnected())
                mDelay = System.currentTimeMillis() - startTime;
            else
                mError = Cmd.TCP_LINK_ERROR;
        } catch (UnknownHostException e) {
            mError = Cmd.UNKNOWN_HOST_ERROR;
        } catch (IOException e) {
            mError = Cmd.TCP_LINK_ERROR;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void cancel() {

    }

    /**
     * Is connected service port
     *
     * @return Is Connected
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Get connected delay time
     *
     * @return Connected delay
     */
    public long getDelay() {
        return mDelay;
    }

    @Override
    public String toString() {
        return "Port:" + mPort +
                " Delay:" + mDelay +
                " Connected:" + isConnected;
    }
}
