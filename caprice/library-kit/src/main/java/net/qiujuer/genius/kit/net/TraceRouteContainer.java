/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 01/14/2015
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

import java.util.Comparator;

/**
 * Routing information to {@link net.qiujuer.genius.kit.net.TraceRoute}
 */
class TraceRouteContainer {
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
