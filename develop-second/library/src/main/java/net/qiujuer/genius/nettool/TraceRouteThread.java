/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 01/14/2015
 * Changed 01/14/2015
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
package net.qiujuer.genius.nettool;

import net.qiujuer.genius.command.Command;

/**
 * This is run in thread to get ping by ip and ttl values
 * Created by Qiujuer
 * on 2015/1/14.
 */
class TraceRouteThread extends Thread {
    private int mTTL;
    private String mIP;
    private Ping mPing;
    private Command mCommand;
    private TraceThreadInterface mInterface;

    private boolean isArrived;
    private boolean isError;


    public TraceRouteThread(String ip, int ttl, TraceThreadInterface traceThreadInterface) {
        this.mIP = ip;
        this.mTTL = ttl;
        this.mInterface = traceThreadInterface;

        this.setName("TraceThread:" + ip + " " + ttl);
        this.setDaemon(true);
        this.start();
    }

    /**
     * TTL Route
     *
     * @param ip  ip
     * @param ttl ttl
     * @return isError
     */
    private TraceRouteContainer trace(String ip, int ttl) {
        String res = launchRoute(ip, ttl);
        if (!this.isInterrupted() && res != null && res.length() > 0) {
            res = res.toLowerCase();
            if (res.contains(NetModel.PING_EXCEED) || !res.contains(NetModel.PING_UNREACHABLE)) {
                // Succeed
                String pIp = parseIpFromRoute(res);
                if (!this.isInterrupted() && pIp != null && pIp.length() > 0) {
                    mPing = new Ping(4, 32, pIp, false);
                    mPing.start();
                    TraceRouteContainer routeContainer = new TraceRouteContainer(ttl, pIp, mPing.getLossRate(), mPing.getDelay());
                    mPing = null;
                    isArrived = pIp.contains(ip);
                    return routeContainer;
                }
            }
        }
        isError = true;
        return null;
    }

    /**
     * Get TTL IP
     *
     * @param ip  Target IP
     * @param ttl TTL
     * @return Ping IP and TTL Result
     */
    private String launchRoute(String ip, int ttl) {
        mCommand = new Command("/system/bin/ping",
                "-c", "4",
                "-s", "32",
                "-t", String.valueOf(ttl),
                ip);

        String str = null;
        try {
            str = Command.command(mCommand);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mCommand = null;
        }
        return str;
    }

    /**
     * Parsing IP address
     *
     * @param ping Ping IP and TTL Result
     * @return IP Address
     */
    private String parseIpFromRoute(String ping) {
        String ip = null;
        try {
            if (ping.contains(NetModel.PING_FROM)) {
                // Get ip when ttl exceeded
                int index = ping.indexOf(NetModel.PING_FROM);
                ip = ping.substring(index + 5);
                if (ip.contains(NetModel.PING_PAREN_THESE_OPEN)) {
                    int indexOpen = ip.indexOf(NetModel.PING_PAREN_THESE_OPEN);
                    int indexClose = ip.indexOf(NetModel.PING_PAREN_THESE_CLOSE);
                    ip = ip.substring(indexOpen + 1, indexClose);
                } else {
                    // Get ip when after from
                    ip = ip.substring(0, ip.indexOf("\n"));
                    if (ip.contains(":"))
                        index = ip.indexOf(":");
                    else
                        index = ip.indexOf(" ");
                    ip = ip.substring(0, index);
                }
            } else if (ping.contains(NetModel.PING)) {
                int indexOpen = ping.indexOf(NetModel.PING_PAREN_THESE_OPEN);
                int indexClose = ping.indexOf(NetModel.PING_PAREN_THESE_CLOSE);
                ip = ping.substring(indexOpen + 1, indexClose);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ip;
    }

    @Override
    public void run() {
        super.run();
        TraceRouteContainer routeContainer = trace(mIP, mTTL);
        mInterface.complete(this, this.isError, this.isArrived, routeContainer);
        mInterface = null;
    }

    /**
     * Cancel
     */
    public void cancel() {
        if (mPing != null)
            mPing.cancel();
        if (mCommand != null)
            Command.cancel(mCommand);
        try {
            this.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static interface TraceThreadInterface {
        void complete(TraceRouteThread trace, boolean isError, boolean isArrived, TraceRouteContainer routeContainer);
    }
}
