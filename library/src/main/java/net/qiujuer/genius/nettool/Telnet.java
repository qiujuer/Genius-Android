/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 12/25/2014
 * Changed 12/25/2014
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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @author QiuJu
 */
public class Telnet extends NetModel {
    protected static final int TimeOutSize = 3000;
    private String host;
    private int port;
    private long delay;
    private boolean isConnected;

    public Telnet(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void start() {
        Socket socket = null;
        try {
            Long bTime = System.currentTimeMillis();
            socket = new Socket();
            try {
                socket.setSoTimeout(TimeOutSize);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            socket.connect(new InetSocketAddress(host, port), TimeOutSize);
            if (isConnected = socket.isConnected())
                delay = System.currentTimeMillis() - bTime;
            else
                error = TCP_LINK_ERROR;
        } catch (UnknownHostException e) {
            error = UNKNOWN_HOST_ERROR;
        } catch (IOException e) {
            error = TCP_LINK_ERROR;
        } finally {
            if (socket != null)
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    @Override
    public void cancel() {

    }

    public boolean isConnected() {
        return isConnected;
    }

    public long getDelay() {
        return delay;
    }

    @Override
    public String toString() {
        return "Port:" + port +
                " Delay:" + delay +
                " Connected:" + isConnected;
    }
}
