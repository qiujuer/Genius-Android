/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 09/21/2014
 * Changed 03/08/2015
 * Version 3.0.0
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
package net.qiujuer.genius.kit.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by QiuJu
 * on 2014/9/21.
 */
public class TraceRoute extends NetModel implements TraceRouteThread.TraceThreadInterface {
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

    @Override
    public void complete(TraceRouteThread trace, boolean isError, boolean isArrived, TraceRouteContainer routeContainer) {
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

    /**
     * Override Start
     */
    @Override
    public void start() {
        // Get IPs
        DnsResolve dns = new DnsResolve(mTarget);
        dns.start();
        List<String> ips = dns.getAddresses();
        if (dns.getError() != NetModel.SUCCEED || ips == null || ips.size() == 0)
            return;
        mIP = ips.get(0);

        // Init List
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

    @Override
    public String toString() {
        return "IP:" + mIP + " Routes:" + (mRoutes == null ? "[]" : mRoutes.toString());
    }
}