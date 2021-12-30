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
import com.galaxy.lemon.common.utils.StringUtils;
import com.galaxy.lemon.framework.gray.GrayAvoidanceServerPredicate;
import com.galaxy.lemon.framework.gray.GrayMatchServerPredicate;
import com.galaxy.lemon.framework.gray.GrayUtils;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.CompositePredicate;
import com.netflix.loadbalancer.PredicateKey;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class CompositePredicateGrayDecorator {

    private static final Field FIELD_DELEGATE = ReflectionUtils.findField(CompositePredicate.class, "delegate");
    private static final Field FIELD_FALLBACKS = ReflectionUtils.findField(CompositePredicate.class, "fallbacks");
    public static final String METHOD_CHOOSE_ROUND_ROBIN_AFTER_FILTERING = "chooseRoundRobinAfterFiltering";
    public static final int METHOD_ARGUMENTS_COUNT_CHOOSE_ROUND_ROBIN_AFTER_FILTERING = 2;

    static {
        ReflectionUtils.makeAccessible(FIELD_DELEGATE);
        ReflectionUtils.makeAccessible(FIELD_FALLBACKS);
    }
    /**
     *
     * @param compositePredicate
     * @return
     */
    public CompositePredicate decorate(CompositePredicate compositePredicate) {
        AbstractServerPredicate originalDelegate = getCompositePredicateDelegate(compositePredicate);
        GrayMatchServerPredicate grayMatchServerPredicate = new GrayMatchServerPredicate();
        Predicate<PredicateKey> graySupportedDelegate = Predicates.and(originalDelegate, grayMatchServerPredicate);
        setCompositePredicateDelegate(compositePredicate, AbstractServerPredicate.ofKeyPredicate(graySupportedDelegate));


        GrayAvoidanceServerPredicate grayAvoidanceServerPredicate = new GrayAvoidanceServerPredicate();
        List<AbstractServerPredicate> graySupportFallbacks = new ArrayList<>();
        for (AbstractServerPredicate fallbackServerPredicate : getCompositePredicateFallbacks(compositePredicate)) {
            Predicate<PredicateKey> fallChain = Predicates.and(grayAvoidanceServerPredicate, fallbackServerPredicate);
            graySupportFallbacks.add(AbstractServerPredicate.ofKeyPredicate(fallChain));
        }
        setCompositePredicateFallbacks(compositePredicate, graySupportFallbacks);

        return (CompositePredicate)Proxy.getProxy(CompositePredicate.class).newInstance(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                if (isChooseRoundRobinAfterFilteringMethod(method)
                        && objects.length == METHOD_ARGUMENTS_COUNT_CHOOSE_ROUND_ROBIN_AFTER_FILTERING
                        && null == objects[1]) {
                    objects[1] = GrayUtils.getRequestGrayVersion();
                }
                return method.invoke(o, objects);
            }
        });
    }

    private AbstractServerPredicate getCompositePredicateDelegate(CompositePredicate compositePredicate) {
        return (AbstractServerPredicate) ReflectionUtils.getField(FIELD_DELEGATE, compositePredicate);
    }

    private void setCompositePredicateDelegate(CompositePredicate compositePredicate, AbstractServerPredicate delegate) {
        ReflectionUtils.setField(FIELD_DELEGATE, compositePredicate, delegate);
    }

    private List<AbstractServerPredicate> getCompositePredicateFallbacks(CompositePredicate compositePredicate) {
        return (List<AbstractServerPredicate>)ReflectionUtils.getField(FIELD_FALLBACKS, compositePredicate);
    }

    private void setCompositePredicateFallbacks(CompositePredicate compositePredicate, List<AbstractServerPredicate> fallbacks) {
        ReflectionUtils.setField(FIELD_FALLBACKS, compositePredicate, fallbacks);
    }

    private boolean isChooseRoundRobinAfterFilteringMethod(Method method) {
        return StringUtils.equals(METHOD_CHOOSE_ROUND_ROBIN_AFTER_FILTERING, method.getName())
                && method.getParameterCount() == METHOD_ARGUMENTS_COUNT_CHOOSE_ROUND_ROBIN_AFTER_FILTERING;
    }
}
