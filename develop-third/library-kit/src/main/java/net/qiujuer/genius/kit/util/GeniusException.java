/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 11/24/2014
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
package net.qiujuer.genius.kit.util;

/**
 * GeniusException in this lib
 * extends {@link #RuntimeException}
 */
public class GeniusException extends RuntimeException {

    private static final long serialVersionUID = -1L;

    public GeniusException(String detailMessage) {
        super(detailMessage);
    }

    public GeniusException(Throwable throwable) {
        super(throwable);
    }

    public GeniusException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

}