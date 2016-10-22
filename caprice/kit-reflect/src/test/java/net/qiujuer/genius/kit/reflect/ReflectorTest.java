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

import org.junit.Test;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

/**
 * The {@link Reflector} test
 */
public class ReflectorTest {
    private static class UserBean {
    }

    private static class ResultBean<M> {
    }

    private static abstract class BaseGeneric<T, V> {
    }

    private static abstract class ChildOne<T> extends BaseGeneric<T, UserBean> {
    }

    private static abstract class ChildTwo<M> extends ChildOne<ResultBean<M>> {
    }

    @Test
    public void getActualTypeArgumentsStatic() throws Exception {
        ChildTwo childTwo = new ChildTwo<UserBean>() {
        };
        Type[] types = Reflector.getActualTypeArguments(BaseGeneric.class, childTwo.getClass());

        assertEquals(true, types.length == 2);
        assertEquals(true, types[0] instanceof ParameterizeTypeActualArgsDelegate);

        ParameterizeTypeActualArgsDelegate delegate = ((ParameterizeTypeActualArgsDelegate) types[0]);

        assertEquals(ResultBean.class, delegate.getRawType());
        assertEquals(true, delegate.getActualTypeArguments().length == 1);
        assertEquals(UserBean.class, delegate.getActualTypeArguments()[0]);
        assertEquals(UserBean.class, types[1]);
        Logger.getLogger("ReflectorTest").log(Level.INFO, Arrays.toString(types));
    }
}
