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

/**
 * Ping class extends {@link AbsNet}
 * Ping url or ip
 */
public class Ping extends AbsNet {
    private String mTarget;
    private String mIp = null;
    private float mLossRate = 1f;
    private float mDelay = 0;
    private float mTotalTime = 0;

    private transient boolean isAnalysisIp;
    private transient int mCount, mSize;
    private transient Command mCommand;

    /**
     * To specify the IP or domain name to Ping test and return the IP, packet loss,
     * delay parameter to specify the IP or domain name such as Ping test and return the IP,
     * packet loss, delay and other parameters
     *
     * @param target The target
     */
    public Ping(String target) {
        this(4, 32, target, true);
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
     * @param count        Packets
     * @param size         Packet size
     * @param target       The target
     * @param isAnalysisIp Whether parsing IP
     */
    public Ping(int count, int size, String target, boolean isAnalysisIp) {
        this.isAnalysisIp = isAnalysisIp;
        this.mCount = count;
        this.mSize = size;
        this.mTarget = target;
    }

    /**
     * *********************************************************************************************
     * To parse and load
     * *********************************************************************************************
     */
    private String launchPing() {
        long startTime = System.currentTimeMillis();
        mCommand = new Command("/system/bin/ping",
                "-c", String.valueOf(mCount),
                "-s", String.valueOf(mSize),
                mTarget);
        try {
            String res = Command.command(mCommand);
            mTotalTime = (System.currentTimeMillis() - startTime);
            return res;
        } catch (Exception e) {
            cancel();
            return null;
        } finally {
            mCommand = null;
        }
    }

    private String parseIp(String ping) {
        String ip = null;
        try {
            if (ping.contains(Cmd.PING)) {
                int indexOpen = ping.indexOf(Cmd.PING_PAREN_THESE_OPEN);
                int indexClose = ping.indexOf(Cmd.PING_PAREN_THESE_CLOSE);
                ip = ping.substring(indexOpen + 1, indexClose);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ip;
    }

    private float parseLoss(String ping) {
        float transmit = 0f, error = 0f, receive = 0f, lossRate = 0f;
        try {
            if (ping.contains(Cmd.PING_STATISTICS)) {
                String lossStr = ping.substring(ping.indexOf(Cmd.PING_BREAK_LINE, ping.indexOf(Cmd.PING_STATISTICS)) + 1);
                lossStr = lossStr.substring(0, lossStr.indexOf(Cmd.PING_BREAK_LINE));
                String strArray[] = lossStr.split(Cmd.PING_COMMA);
                for (String str : strArray) {
                    if (str.contains(Cmd.PING_TRANSMIT))
                        transmit = Float.parseFloat(str.substring(0, str.indexOf(Cmd.PING_TRANSMIT)));
                    else if (str.contains(Cmd.PING_RECEIVED))
                        receive = Float.parseFloat(str.substring(0, str.indexOf(Cmd.PING_RECEIVED)));
                    else if (str.contains(Cmd.PING_ERRORS))
                        error = Float.parseFloat(str.substring(0, str.indexOf(Cmd.PING_ERRORS)));
                    else if (str.contains(Cmd.PING_LOSS))
                        lossRate = Float.parseFloat(str.substring(0, str.indexOf(Cmd.PING_RATE)));
                }
            }
            if (transmit != 0)
                lossRate = error / transmit;
            else if (lossRate == 0)
                lossRate = error / (error + receive);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lossRate;
    }

    private float parseDelay(String ping) {
        float delay = 0;
        try {
            if (ping.contains(Cmd.PING_RTT)) {
                String lossStr = ping.substring(ping.indexOf(Cmd.PING_RTT));
                lossStr = lossStr.substring(lossStr.indexOf(Cmd.PING_EQUAL) + 2);
                String strArray[] = lossStr.split(Cmd.PING_SLASH);
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
            if (res.contains(Cmd.PING_UNREACHABLE) && !res.contains(Cmd.PING_EXCEED)) {
                // Failed
                mLossRate = 1f;
                mError = Cmd.HOST_UNREACHABLE_ERROR;
            } else {
                // Succeed
                mLossRate = parseLoss(res);
                mDelay = parseDelay(res);
                if (isAnalysisIp)
                    mIp = parseIp(res);
            }
        } else {
            mError = Cmd.DROP_DATA_ERROR;
        }
    }

    @Override
    public void cancel() {
        if (mCommand != null)
            Command.cancel(mCommand);
    }

    /**
     * Get ping return ip
     *
     * @return AnalysisIp
     * @see #isAnalysisIp if this is false renturn null
     */
    public String getIp() {
        return mIp;
    }

    /**
     * Get loss rate
     *
     * @return LossRate
     */
    public float getLossRate() {
        return mLossRate;
    }

    /**
     * Get delay time
     *
     * @return Delay time
     */
    public float getDelay() {
        return mDelay;
    }

    /**
     * Use total time
     *
     * @return Total time
     */
    public float getTotalTime() {
        return mTotalTime;
    }

    @Override
    public String toString() {
        return "IP:" + mIp +
                " LossRate:" + mLossRate +
                " Delay:" + mDelay +
                " TotalTime:" + mTotalTime;
    }
}
