package net.qiujuer.genius.nettool;

import net.qiujuer.genius.command.Command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by QiuJu
 * on 2014/9/21.
 */
public class TraceRoute extends NetModel {
    private final static int OnceCount = 4;
    private final static int ForCount = 5;
    private final Object traceLock = new Object();

    private String target;
    private String ip;
    private List<String> routes = null;

    private transient int errorCount = 0;
    private transient boolean done = false;
    private transient boolean isArrived = false;
    private transient List<RouteContainer> routeContainers = null;
    private transient List<TraceThread> threads = null;
    private transient CountDownLatch mLatch = null;

    /**
     * TraceRoute domain or ip
     * Return the  domain or ip route result
     *
     * @param target domain or ip
     */
    public TraceRoute(String target) {
        this.target = target;
    }


    /**
     * Clear List
     */
    private void clear() {
        if (threads != null) {
            synchronized (traceLock) {
                for (TraceThread thread : threads) {
                    thread.cancel();
                }
            }
        }
    }

    private void complete(TraceThread trace, boolean isError, boolean isArrived, RouteContainer routeContainer) {
        if (threads != null) {
            synchronized (traceLock) {
                try {
                    threads.remove(trace);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!done) {
            if (isError)
                this.errorCount++;
            this.isArrived = isArrived;
            if (routeContainers != null && routeContainer != null)
                routeContainers.add(routeContainer);
        }
        if (mLatch != null && mLatch.getCount() > 0)
            mLatch.countDown();
    }

    /**
     * Override Start
     */
    @Override
    public void start() {
        DnsResolve dns = new DnsResolve(target);
        dns.start();
        List<String> ips = dns.getAddresses();
        if (dns.getError() != NetModel.SUCCEED || ips == null || ips.size() == 0)
            return;
        ip = ips.get(0);

        routeContainers = new ArrayList<RouteContainer>();
        threads = new ArrayList<TraceThread>(OnceCount);

        for (int i = 0; i < ForCount; i++) {
            mLatch = new CountDownLatch(OnceCount);
            synchronized (traceLock) {
                for (int j = 1; j <= OnceCount; j++) {
                    //get ttl
                    final int ttl = i * OnceCount + j;
                    //thread run get tp ttl ping information
                    threads.add(new TraceThread(ip, ttl));
                }
            }
            try {
                mLatch.await(40, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //end
            if (mLatch.getCount() > 0) {
                clear();
            }

            //clear
            mLatch = null;
            synchronized (traceLock) {
                threads.clear();
            }

            if (done || isArrived || errorCount > OnceCount - 1)
                break;
        }

        if (routeContainers.size() > 0) {
            //sort
            Collections.sort(routeContainers, new RouteContainerComparator());
            ArrayList<String> routes = new ArrayList<String>();
            int size = routeContainers.size();
            for (int s = 0; s < size; s++) {
                if (s > 0 && routeContainers.get(s).ip.equals(routeContainers.get(s - 1).ip)) {
                    break;
                } else {
                    routes.add(routeContainers.get(s).toString());
                }
            }

            routes.trimToSize();
            this.routes = routes;
        }

        routeContainers = null;
        threads = null;
    }

    /**
     * Override Cancel
     */
    @Override
    public void cancel() {
        done = true;
        clear();
    }

    /**
     * The Routes Target IP
     *
     * @return IP Address
     */
    public String getIp() {
        return ip;
    }

    /**
     * For routing values
     *
     * @return Routes
     */
    public List<String> getRoutes() {
        return routes;
    }

    /**
     * Trace Route Thread
     */
    class TraceThread extends Thread {
        private String ip;
        private int ttl;
        private Ping ping;
        private Command command;
        private boolean isArrived;
        private boolean isError;

        public TraceThread(String ip, int ttl) {
            this.ip = ip;
            this.ttl = ttl;
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
        private RouteContainer trace(String ip, int ttl) {
            String res = launchRoute(ip, ttl);
            if (!this.isInterrupted() && res != null && res.length() > 0) {
                res = res.toLowerCase();
                if (res.contains(PING_EXCEED) || !res.contains(PING_UNREACHABLE)) {
                    // succeed
                    String pIp = parseIpFromRoute(res);
                    if (!this.isInterrupted() && pIp != null && pIp.length() > 0) {
                        ping = new Ping(4, 32, pIp, false);
                        ping.start();
                        RouteContainer routeContainer = new RouteContainer(ttl, pIp, ping.getLossRate(), ping.getDelay());
                        ping = null;
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
            command = new Command("/system/bin/ping",
                    "-c", "4",
                    "-s", "32",
                    "-t", String.valueOf(ttl),
                    ip);

            String str = null;
            try {
                str = Command.command(command);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                command = null;
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
                if (ping.contains(PING_FROM)) {
                    // Get ip when ttl exceeded
                    int index = ping.indexOf(PING_FROM);
                    ip = ping.substring(index + 5);
                    if (ip.contains(PING_PAREN_THESE_OPEN)) {
                        int indexOpen = ip.indexOf(PING_PAREN_THESE_OPEN);
                        int indexClose = ip.indexOf(PING_PAREN_THESE_CLOSE);
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
                } else if (ping.contains(PING)) {
                    int indexOpen = ping.indexOf(PING_PAREN_THESE_OPEN);
                    int indexClose = ping.indexOf(PING_PAREN_THESE_CLOSE);
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
            RouteContainer routeContainer = trace(ip, ttl);
            complete(this, this.isError, this.isArrived, routeContainer);
        }

        /**
         * Cancel Test
         */
        public void cancel() {
            if (ping != null)
                ping.cancel();
            if (command != null)
                Command.cancel(command);
            try {
                this.interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Routing information
     */
    class RouteContainer {
        public String ip;
        public int ttl;
        public float loss;
        public float delay;

        public RouteContainer(int ttl, String ip, float loss, float delay) {
            this.ttl = ttl;
            this.ip = ip;
            this.loss = loss;
            this.delay = delay;
        }

        @Override
        public String toString() {
            return "Ttl:" + ttl + " " + ip + " Loss:" + loss + " Delay:" + delay;
        }
    }

    /**
     * The TraceRoute results are sorted to sort the TraceRoute result
     */
    class RouteContainerComparator implements Comparator<RouteContainer> {
        public int compare(RouteContainer routeContainer1, RouteContainer routeContainer2) {
            if (routeContainer1 == null)
                return 1;
            if (routeContainer2 == null)
                return -1;
            if (routeContainer1.ttl < routeContainer2.ttl)
                return -1;
            else if (routeContainer1.ttl == routeContainer2.ttl)
                return 0;
            else
                return 1;
        }
    }

    @Override
    public String toString() {
        return "IP:" + ip + " Routes:" + routes;
    }
}