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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * TraceRoute class
 * extends {@link AbsNet}
 * <p>
 * TraceRoute url or ip
 */
public class TraceRoute extends AbsNet {
    private final static int ONCE_COUNT = Runtime.getRuntime().availableProcessors();
    private final static int LOOP_COUNT = 30 / ONCE_COUNT;

    private final Object mLock = new Object();
    private String mTarget;
    private String mIP;
    private List<String> mRoutes = null;

    private transient int errorCount = 0;
    private transient boolean isDone = false;
    private transient boolean isArrived = false;
    private transient List<TraceRouteContainer> routeContainers = null;
    private transient List<TraceRouteThread> threads = null;
    private transient CountDownLatch countDownLatch = null;

    /**
     * TraceRoute domain or ip
     * Return the  domain or ip route result
     *
     * @param target domain or ip
     */
    public TraceRoute(String target) {
        this.mTarget = target;
    }

    /**
     * Clear List
     */
    private void clear() {
        if (threads != null) {
            synchronized (mLock) {
                for (TraceRouteThread thread : threads) {
                    thread.cancel();
                }
            }
        }
    }

    /**
     * Override Start
     */
    @Override
    public void start() {
        // Get IPs
        DnsResolve dns = new DnsResolve(mTarget);
        dns.start();
        List<String> ips = dns.getAddresses();
        if (dns.getError() != Cmd.SUCCEED || ips == null || ips.size() == 0)
            return;
        mIP = ips.get(0);

        // Cmd List
        routeContainers = new ArrayList<>();
        threads = new ArrayList<>(ONCE_COUNT);

        // Loop
        for (int i = 0; i < LOOP_COUNT; i++) {
            countDownLatch = new CountDownLatch(ONCE_COUNT);
            synchronized (mLock) {
                for (int j = 1; j <= ONCE_COUNT; j++) {
                    // Get ttl
                    final int ttl = i * ONCE_COUNT + j;
                    // Thread run get tp ttl ping information
                    threads.add(new TraceRouteThread(mIP, ttl, this));
                }
            }
            // Await 40 seconds long time
            try {
                countDownLatch.await(40, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // End
            if (countDownLatch.getCount() > 0) {
                clear();
            }

            // Clear
            countDownLatch = null;
            synchronized (mLock) {
                threads.clear();
            }

            // Break Loop
            if (isDone || isArrived || errorCount > 3)
                break;
        }

        // Set Result
        if (routeContainers.size() > 0) {
            // Sort
            Collections.sort(routeContainers, new TraceRouteContainer.TraceRouteContainerComparator());

            // Set values
            ArrayList<String> routes = new ArrayList<>();
            int size = routeContainers.size();
            String prevIP = null;

            // For
            for (int s = 0; s < size; s++) {
                TraceRouteContainer container = routeContainers.get(s);
                if (prevIP != null && container.mIP.equals(prevIP)) {
                    break;
                } else {
                    routes.add(container.toString());
                    prevIP = container.mIP;
                }
            }

            routes.trimToSize();
            mRoutes = routes;
        }

        // Clear
        routeContainers = null;
        threads = null;
    }

    /**
     * Override Cancel
     */
    @Override
    public void cancel() {
        isDone = true;
        clear();
    }

    /**
     * The Routes Target IP
     *
     * @return IP Address
     */
    public String getAddress() {
        return mIP;
    }

    /**
     * For routing values
     *
     * @return Routes
     */
    public List<String> getRoutes() {
        return mRoutes;
    }

    /**
     * Will the run thread is complete should call this method
     *
     * @param trace          TraceRouteThread
     * @param isError        The run status
     * @param isArrived      Is Arrived the ip
     * @param routeContainer TraceRouteContainer
     */
    void onComplete(TraceRouteThread trace, boolean isError, boolean isArrived, TraceRouteContainer routeContainer) {
        if (threads != null) {
            synchronized (mLock) {
                try {
                    threads.remove(trace);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!isDone) {
            if (isError)
                this.errorCount++;
            this.isArrived = isArrived;
            if (routeContainers != null && routeContainer != null)
                routeContainers.add(routeContainer);
        }
        if (countDownLatch != null && countDownLatch.getCount() > 0)
            countDownLatch.countDown();
    }

    @Override
    public String toString() {
        return "IP:" + mIP + " Routes:" + (mRoutes == null ? "[]" : mRoutes.toString());
    }

    /**
     * Routing information to {@link TraceRoute}
     */
    static class TraceRouteContainer {
        public String mIP;
        public int mTTL;
        public float mLoss;
        public float mDelay;

        public TraceRouteContainer(int ttl, String ip, float loss, float delay) {
            this.mTTL = ttl;
            this.mIP = ip;
            this.mLoss = loss;
            this.mDelay = delay;
        }

        @Override
        public String toString() {
            return "Ttl:" + mTTL + " " + mIP + " Loss:" + mLoss + " Delay:" + mDelay;
        }

        /**
         * The TraceRouteRoute results are sorted to sort the TraceRouteThread result
         */
        protected static class TraceRouteContainerComparator implements Comparator<TraceRouteContainer> {
            public int compare(TraceRouteContainer container1, TraceRouteContainer container2) {
                if (container1 == null)
                    return 1;
                if (container2 == null)
                    return -1;
                if (container1.mTTL < container2.mTTL)
                    return -1;
                else if (container1.mTTL == container2.mTTL)
                    return 0;
                else
                    return 1;
            }
        }
    }


    /**
     * This is run in thread to get ping by ip and ttl values
     * extends {@link #Thread}
     */
    static class TraceRouteThread extends Thread {
        private int mTTL;
        private String mIP;
        private Ping mPing;
        private Command mCommand;
        private TraceRoute mInterface;

        private boolean isArrived;
        private boolean isError;


        public TraceRouteThread(String ip, int ttl, TraceRoute traceThreadInterface) {
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
                if (res.contains(Cmd.PING_EXCEED) || !res.contains(Cmd.PING_UNREACHABLE)) {
                    // Succeed
                    String pIp = parseIpFromRoute(res);
                    if (!this.isInterrupted() && pIp != null && pIp.length() > 0) {
                        Ping ping = new Ping(4, 32, pIp, false);
                        mPing = ping;
                        ping.start();
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
                if (ping.contains(Cmd.PING_FROM)) {
                    // Get ip when ttl exceeded
                    int index = ping.indexOf(Cmd.PING_FROM);
                    ip = ping.substring(index + 5);
                    if (ip.contains(Cmd.PING_PAREN_THESE_OPEN)) {
                        int indexOpen = ip.indexOf(Cmd.PING_PAREN_THESE_OPEN);
                        int indexClose = ip.indexOf(Cmd.PING_PAREN_THESE_CLOSE);
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
                } else if (ping.contains(Cmd.PING)) {
                    int indexOpen = ping.indexOf(Cmd.PING_PAREN_THESE_OPEN);
                    int indexClose = ping.indexOf(Cmd.PING_PAREN_THESE_CLOSE);
                    ip = ping.substring(indexOpen + 1, indexClose);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ip;
        }

        @Override
        public void run() {
            TraceRouteContainer routeContainer = trace(mIP, mTTL);
            mInterface.onComplete(this, this.isError, this.isArrived, routeContainer);
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
    }
}