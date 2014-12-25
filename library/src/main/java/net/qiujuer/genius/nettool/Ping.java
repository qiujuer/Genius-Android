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

import net.qiujuer.genius.command.Command;

/**
 * Created by QiuJu
 * on 2014/9/21.
 */
public class Ping extends NetModel {
    private boolean analysisIp;
    private int count, size;
    private String target;

    private String ip = null;
    private float lossRate = 1f;
    private float delay = 0;
    private float totalTime = 0;
    private transient Command command;


    /**
     * To specify the IP or domain name to Ping test and return the IP, packet loss,
     * delay parameter to specify the IP or domain name such as Ping test and return the IP,
     * packet loss, delay and other parameters
     *
     * @param aim The target
     */
    public Ping(String aim) {
        this(4, 32, aim, true);
    }

    /**
     * To specify the IP or domain name to Ping test and return the IP, packet loss,
     * delay parameter to specify the IP or domain name such as Ping test and return the IP,
     * packet loss, delay and other parameters
     *
     * @param count  Packets
     * @param size   Packet size
     * @param target The target
     */
    public Ping(int count, int size, String target) {
        this(count, size, target, true);
    }

    /**
     * To specify the IP or domain name to Ping test and return the IP, packet loss,
     * delay parameter to specify the IP or domain name such as Ping test and return the IP,
     * packet loss, delay and other parameters
     *
     * @param count      Packets
     * @param size       Packet size
     * @param target     The target
     * @param analysisIp Whether parsing IP
     */
    public Ping(int count, int size, String target, boolean analysisIp) {
        this.analysisIp = analysisIp;
        this.count = count;
        this.size = size;
        this.target = target;
    }

    /**
     * *********************************************************************************************
     * To parse and load
     * *********************************************************************************************
     */
    private String launchPing() {
        long startTime = System.currentTimeMillis();
        command = new Command("/system/bin/ping",
                "-c", String.valueOf(count),
                "-s", String.valueOf(size),
                target);
        try {
            String res = Command.command(command);
            command = null;
            totalTime = (System.currentTimeMillis() - startTime);
            return res;
        } catch (Exception e) {
            return null;
        }
    }

    private String parseIpFromPing(String ping) {
        String ip = null;
        try {
            if (ping.contains(NetModel.PING)) {
                int indexOpen = ping.indexOf(NetModel.PING_PAREN_THESE_OPEN);
                int indexClose = ping.indexOf(NetModel.PING_PAREN_THESE_CLOSE);
                ip = ping.substring(indexOpen + 1, indexClose);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ip;
    }

    private float parseLossFromPing(String ping) {
        float transmit = 0f, error = 0f, receive = 0f, lossRate = 0f;
        try {
            if (ping.contains(NetModel.PING_STATISTICS)) {
                String lossStr = ping.substring(ping.indexOf(NetModel.PING_BREAK_LINE, ping.indexOf(NetModel.PING_STATISTICS)) + 1);
                lossStr = lossStr.substring(0, lossStr.indexOf(NetModel.PING_BREAK_LINE));
                String strArray[] = lossStr.split(NetModel.PING_COMMA);
                for (String str : strArray) {
                    if (str.contains(NetModel.PING_TRANSMIT))
                        transmit = Float.parseFloat(str.substring(0, str.indexOf(NetModel.PING_TRANSMIT)));
                    else if (str.contains(NetModel.PING_RECEIVED))
                        receive = Float.parseFloat(str.substring(0, str.indexOf(NetModel.PING_RECEIVED)));
                    else if (str.contains(NetModel.PING_ERRORS))
                        error = Float.parseFloat(str.substring(0, str.indexOf(NetModel.PING_ERRORS)));
                    else if (str.contains(NetModel.PING_LOSS))
                        lossRate = Float.parseFloat(str.substring(0, str.indexOf(NetModel.PING_RATE)));
                }
            }
            if (transmit != 0)
                return error / transmit;
            else if (lossRate == 0)
                return error / (error + receive);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lossRate;
    }

    private float parseDelayFromPing(String ping) {
        float delay = 0;
        try {
            if (ping.contains(NetModel.PING_RTT)) {
                String lossStr = ping.substring(ping.indexOf(NetModel.PING_RTT));
                lossStr = lossStr.substring(lossStr.indexOf(NetModel.PING_EQUAL) + 2);
                String strArray[] = lossStr.split(NetModel.PING_SLASH);
                delay = Float.parseFloat(strArray[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return delay;
    }

    /**
     * *********************************************************************************************
     * Public  method
     * *********************************************************************************************
     */

    @Override
    public void start() {
        String res = launchPing();
        if (res != null && res.length() > 0) {
            res = res.toLowerCase();
            if (res.contains(NetModel.PING_UNREACHABLE) && !res.contains(NetModel.PING_EXCEED)) {
                // failed
                lossRate = 1f;
                error = HOST_UNREACHABLE_ERROR;
            } else {
                // succeed
                lossRate = parseLossFromPing(res);
                delay = parseDelayFromPing(res);
                if (analysisIp)
                    ip = parseIpFromPing(res);
            }
        } else {
            error = DROP_DATA_ERROR;
        }
    }

    @Override
    public void cancel() {
        if (command != null)
            Command.cancel(command);
    }

    public String getIp() {
        return ip;
    }

    public float getLossRate() {
        return lossRate;
    }

    public float getDelay() {
        return delay;
    }

    public float getTotalTime() {
        return totalTime;
    }

    @Override
    public String toString() {
        return "IP:" + ip +
                " LossRate:" + lossRate +
                " Delay:" + delay +
                " TotalTime:" + totalTime;
    }
}
