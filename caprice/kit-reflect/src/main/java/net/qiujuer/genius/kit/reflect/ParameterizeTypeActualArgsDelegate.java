/*
 * Copyright (C) 2014-2016 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Author qiujuer
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
package net.qiujuer.genius.kit.reflect;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * This class implements {@link ParameterizedType}, {@link Serializable}
 * The class be used to delegate {@link ParameterizedType#getActualTypeArguments()} method,
 * Returned by call {@link #getActualTypeArguments()}.
 */
class ParameterizeTypeActualArgsDelegate implements ParameterizedType,
        Serializable {
    private static final long serialVersionUID = 246138727267926807L;
    private ParameterizedType delegateType;
    private Type[] actualArgs;

    ParameterizeTypeActualArgsDelegate(ParameterizedType delegateType, Type[] actualArgs) {
        this.delegateType = delegateType;
        if (actualArgs != null)
            this.actualArgs = actualArgs;
        else
            this.actualArgs = new Type[0];
    }

    @Override
    public Type[] getActualTypeArguments() {
        return actualArgs;
    }

    @Override
    public Type getRawType() {
        return delegateType.getRawType();
    }

    @Override
    public Type getOwnerType() {
        return delegateType.getOwnerType();
    }

    @Override
    public String toString() {
        Type rawType = getRawType();
        if (rawType == null) {
            return super.toString();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(rawType instanceof Class ? ((Class) rawType).getName() : rawType.toString());
        Type[] args = getActualTypeArguments();
        if (args != null && args.length > 0) {
            sb.append("<");
            sb.append(args[0].toString());

            for (int i = 1; i < args.length; i++) {
                sb.append(", ");
                sb.append(args[i].toString());
            }

            sb.append(">");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ParameterizeTypeActualArgsDelegate) {
            ParameterizeTypeActualArgsDelegate in = (ParameterizeTypeActualArgsDelegate) obj;
            return in.delegateType.equals(this.delegateType)
                    && Arrays.equals(this.actualArgs, in.actualArgs);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (delegateType == null ? 0 : delegateType.hashCode())
                ^ Arrays.hashCode(actualArgs);
    }
}