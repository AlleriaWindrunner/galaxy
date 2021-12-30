/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.galaxy.lemon.framework.gray;


import com.galaxy.lemon.common.cglib.Proxy;
import com.galaxy.lemon.framework.data.BaseLemonData;
import com.galaxy.lemon.framework.data.LemonDataHolder;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.netflix.loadbalancer.*;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 灰度规则处理
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class GrayZoneAvoidanceRule extends ZoneAvoidanceRule {
    private static final Field FIELD_DELEGATE = ReflectionUtils.findField(CompositePredicate.class, "delegate");
    private static final Field FIELD_FALLBACKS = ReflectionUtils.findField(CompositePredicate.class, "fallbacks");

    private AbstractServerPredicate cachedServerPredicate;
    private Lock lock = new ReentrantLock();

    static {
        ReflectionUtils.makeAccessible(FIELD_DELEGATE);
        ReflectionUtils.makeAccessible(FIELD_FALLBACKS);
    }

    @Override
    public AbstractServerPredicate getPredicate() {
        if (null == this.cachedServerPredicate) {
            lock.lock();
            try {
                if (null == this.cachedServerPredicate) {
                    this.cachedServerPredicate = createDelegateCompositePredicated();
                }
            } finally {
                lock.unlock();
            }

        }
        return this.cachedServerPredicate;
    }

    private AbstractServerPredicate createDelegateCompositePredicated() {
        AbstractServerPredicate serverPredicate = super.getPredicate();
        if (serverPredicate instanceof CompositePredicate) {
            AbstractServerPredicate originalPredicate = (AbstractServerPredicate) ReflectionUtils.getField(FIELD_DELEGATE, serverPredicate);
            GrayMatchServerPredicate grayServerPredicate = new GrayMatchServerPredicate();
            Predicate<PredicateKey> chain = Predicates.<PredicateKey>and(originalPredicate, grayServerPredicate);
            ReflectionUtils.setField(FIELD_DELEGATE, serverPredicate, AbstractServerPredicate.ofKeyPredicate(chain));

            AvailabilityPredicate availabilityPredicate = new AvailabilityPredicate(this, null);
            GrayAvoidanceServerPredicate fallbackServerPredicate = new GrayAvoidanceServerPredicate();
            Predicate<PredicateKey> fallChain = Predicates.<PredicateKey>and(availabilityPredicate, fallbackServerPredicate);
            List<AbstractServerPredicate> fallbacks = Lists.newArrayList();
            fallbacks.add(AbstractServerPredicate.ofKeyPredicate(fallChain));
            ReflectionUtils.setField(FIELD_FALLBACKS, serverPredicate, fallbacks);

            return (AbstractServerPredicate) Proxy.getProxy(CompositePredicate.class).newInstance(new MethodInterceptor() {
                @Override
                public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                    if (method.getName().equals("chooseRoundRobinAfterFiltering")) {
                        Object[] newObjects = new Object[objects.length];
                        System.arraycopy(objects, 0, newObjects, 0, objects.length);
                        BaseLemonData lemonData = LemonDataHolder.getLemonData();
                        String loadBalancerKey = Optional.ofNullable(lemonData.getVersionId()).orElse("");
                        newObjects[objects.length - 1] = loadBalancerKey;
                        return method.invoke(serverPredicate, newObjects);
                    }
                    return method.invoke(serverPredicate, objects);
                }
            });
        }
        return serverPredicate;
    }
}
