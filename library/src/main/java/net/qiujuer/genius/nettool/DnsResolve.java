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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by QiuJu
 * on 2014/9/20.
 */
public class DnsResolve extends NetModel {
    private static final byte[] ID = new byte[]{(byte) 2, (byte) 6};
    private static final int TIME_OUT = 8000;
    private String mHostName;
    private InetAddress mServer;
    private List<String> mIPs;
    private long mDelay;

    /**
     * Domain name resolution test
     *
     * @param hostName Domain name address
     */
    public DnsResolve(String hostName) {
        this(hostName, null);
    }

    /**
     * Domain name resolution test
     *
     * @param hostName Domain name address
     * @param server   Specify the domain name server
     */
    public DnsResolve(String hostName, InetAddress server) {
        this.mHostName = hostName;
        this.mServer = server;
    }

    /**
     * This resolve domain to ips
     *
     * @param domain    Domain Name
     * @param dnsServer DNS Server
     * @return IPs
     */
    private ArrayList<String> resolve(String domain, InetAddress dnsServer) {
        // Pointer
        int pos = 12;
        // Init buffer
        byte[] sendBuffer = new byte[100];
        // Message head
        sendBuffer[0] = ID[0];
        sendBuffer[1] = ID[1];
        sendBuffer[2] = 0x01;
        sendBuffer[3] = 0x00;
        sendBuffer[4] = 0x00;
        sendBuffer[5] = 0x01;
        sendBuffer[6] = 0x00;
        sendBuffer[7] = 0x00;
        sendBuffer[8] = 0x00;
        sendBuffer[9] = 0x00;
        sendBuffer[10] = 0x00;
        sendBuffer[11] = 0x00;

        // Add domain
        String[] part = domain.split("\\.");
        for (String s : part) {
            if (s == null || s.length() <= 0)
                continue;
            int sLength = s.length();
            sendBuffer[pos++] = (byte) sLength;
            int i = 0;
            char[] val = s.toCharArray();
            while (i < sLength) {
                sendBuffer[pos++] = (byte) val[i++];
            }
        }

        // 0 end
        sendBuffer[pos++] = 0x00;
        sendBuffer[pos++] = 0x00;
        // 1 A record query
        sendBuffer[pos++] = 0x01;
        sendBuffer[pos++] = 0x00;
        // Internet record query
        sendBuffer[pos++] = 0x01;

        /**
         * UDP Send
         */
        DatagramSocket ds = null;
        byte[] receiveBuffer = null;
        try {
            ds = new DatagramSocket();
            ds.setSoTimeout(TIME_OUT);

            // Send
            DatagramPacket dp = new DatagramPacket(sendBuffer, pos, dnsServer, 53);
            ds.send(dp);

            // Receive
            dp = new DatagramPacket(new byte[512], 512);
            ds.receive(dp);

            // Copy
            int len = dp.getLength();
            receiveBuffer = new byte[len];
            System.arraycopy(dp.getData(), 0, receiveBuffer, 0, len);
        } catch (UnknownHostException e) {
            mError = UNKNOWN_HOST_ERROR;
        } catch (SocketException e) {
            mError = NETWORK_SOCKET_ERROR;
            e.printStackTrace();
        } catch (IOException e) {
            mError = NETWORK_IO_ERROR;
            e.printStackTrace();
        } finally {
            if (ds != null)
                ds.close();
        }

        /**
         * Resolve data
         */

        // Check is return
        if (mError != SUCCEED || receiveBuffer == null)
            return null;

        // ID
        if (receiveBuffer[0] != ID[0] || receiveBuffer[1] != ID[1]
                || (receiveBuffer[2] & 0x80) != 0x80)
            return null;

        // Count
        int queryCount = (receiveBuffer[4] << 8) | receiveBuffer[5];
        if (queryCount == 0)
            return null;

        int answerCount = (receiveBuffer[6] << 8) | receiveBuffer[7];
        if (answerCount == 0)
            return null;

        // Pointer restore
        pos = 12;

        // Skip the query part head
        for (int i = 0; i < queryCount; i++) {
            while (receiveBuffer[pos] != 0x00) {
                pos += receiveBuffer[pos] + 1;
            }
            pos += 5;
        }

        // Get ip form data
        ArrayList<String> iPs = new ArrayList<String>();
        for (int i = 0; i < answerCount; i++) {
            if (receiveBuffer[pos] == (byte) 0xC0) {
                pos += 2;
            } else {
                while (receiveBuffer[pos] != (byte) 0x00) {
                    pos += receiveBuffer[pos] + 1;
                }
                pos++;
            }
            byte queryType = (byte) (receiveBuffer[pos] << 8 | receiveBuffer[pos + 1]);
            pos += 8;
            int dataLength = (receiveBuffer[pos] << 8 | receiveBuffer[pos + 1]);
            pos += 2;
            // Add ip
            if (queryType == (byte) 0x01) {
                int address[] = new int[4];
                for (int n = 0; n < 4; n++) {
                    address[n] = receiveBuffer[pos + n];
                    if (address[n] < 0)
                        address[n] += 256;
                }
                iPs.add(String.format("%d.%d.%d.%d", address[0], address[1], address[2], address[3]));
            }
            pos += dataLength;
        }
        return iPs;
    }

    @Override
    public void start() {
        long sTime = System.currentTimeMillis();
        if (mServer == null) {
            try {
                InetAddress[] adds = InetAddress.getAllByName(mHostName);
                if (adds != null && adds.length > 0) {
                    mIPs = new ArrayList<>(adds.length);
                    for (InetAddress add : adds)
                        mIPs.add(add.getHostAddress());
                }
            } catch (UnknownHostException e) {
                mError = UNKNOWN_HOST_ERROR;
            } catch (Exception e) {
                mError = UNKNOWN_ERROR;
            }
        } else {
            try {
                mIPs = resolve(mHostName, mServer);
            } catch (Exception e) {
                if (mError == SUCCEED)
                    mError = UNKNOWN_ERROR;
                e.printStackTrace();
            }
        }
        mDelay = System.currentTimeMillis() - sTime;
    }

    @Override
    public void cancel() {

    }

    public List<String> getAddresses() {
        return mIPs;
    }

    public long getDelay() {
        return mDelay;
    }

    @Override
    public String toString() {
        return "Delay:" + mDelay +
                " IPs:" + (mIPs == null ? "[]" : mIPs.toString());
    }
}