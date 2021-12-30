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

package com.galaxy.lemon.common.extension;

import com.galaxy.lemon.common.KVPair;
import com.galaxy.lemon.common.utils.AnnotationUtils;
import com.galaxy.lemon.common.utils.ReflectionUtils;
import com.galaxy.lemon.common.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;


/**
 * SPI 扩展
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class SPIExtensionLoader {
    private static final String PREFIX = "META-INF/services/";
    private static final String PROPERTY_IGNORE_START_CHAR = "#";
    private static final String PROPERTY_KEY_VALUE_SEPARATOR = "=";
    private static final ConcurrentMap<Class<?>, Map<String, Class<?>> > EXTENSION_LOADERS = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Class<?>, List<?> > EXTENSION_SERVICES = new ConcurrentHashMap<>();
    

    /**
     * JAVA SPI机制
     * 获取接口／抽象类的扩展实例，扩展文件在META-INF/services/ 下；文件格式：className
     *
     * @param service
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> getExtensionServices(Class<T> service) {
        if(EXTENSION_SERVICES.containsKey(service)) {
            return (List<T>)EXTENSION_SERVICES.get(service);
        }
        synchronized (SPIExtensionLoader.class) {
            if(! EXTENSION_SERVICES.containsKey(service)) {
                EXTENSION_SERVICES.putIfAbsent(service, loadExtensionServices(service));
            }
        }
        return (List<T>)EXTENSION_SERVICES.get(service);
    }

    public static <T> List<T> loadExtensionServices(Class<T> service) {
        List<T> list = new ArrayList<>();
        ServiceLoader<T> loader = ServiceLoader.load(service);
        Iterator<T> iterator = loader.iterator();
        while (true) {
            if (iterator.hasNext()) {
                list.add(iterator.next());
            } else {
                break;
            }
        }
        return list;
    }
    
    /**
     * 获取适配服务
     * @param service
     * @return
     */
    public static <T> T getExtensionAdaptiveService(Class<T> service) {
        List<T> services = getExtensionServices(service);
        if(CollectionUtils.isEmpty(services)) return null;
        List<T> adaptives = services.stream().parallel().filter(s -> AnnotationUtils.isAnnotationPresent(s.getClass(), Adaptive.class)).collect(Collectors.toList());
        if(adaptives.size() != 1) {
            throw new RuntimeException("SPI service \"" + service + "\" with @Adaptive can only one exists, the number, there are \"" + adaptives.size() + "\" services with @Adaptive.");
        }
        return  adaptives.get(0);
    }
    
    /**
     * 获取Activate服务
     * @param service
     * @return
     */
    public static <T> T getExtensionActivateService(Class<T> service) {
        List<T> services = getExtensionServices(service);
        if(CollectionUtils.isEmpty(services)) return null;
        List<T> activates = services.stream().parallel().filter(s -> AnnotationUtils.isAnnotationPresent(s.getClass(), Activate.class)).collect(Collectors.toList());
        if(activates.size() != 1) {
            throw new RuntimeException("SPI service \""+service+"\" with @Activate can only one exists, the number, there are \""+activates.size()+"\" services with @Activate.");
        }
        return  activates.get(0);
    }
    
    /**
     * 
     * 获取接口／抽象类的扩展类，扩展文件在META-INF/services/ 下；文件格式：key=className
     * 
     * @param service
     * @return
     */
    public static <T> Map<String, Class<?>> getExtensionClasses(Class<T> service) {
        if(EXTENSION_LOADERS.containsKey(service)) {
            return EXTENSION_LOADERS.get(service);
        }
        synchronized (SPIExtensionLoader.class) {
            if(! EXTENSION_LOADERS.containsKey(service)) {
                Map<String, Class<?>> extensionClasses = loadExtensionClasses(service);
                EXTENSION_LOADERS.putIfAbsent(service, extensionClasses);
            }
        }
        return EXTENSION_LOADERS.get(service);
    }
    
    /**
     * 
     * 获取接口／抽象类的扩展类，扩展文件在META-INF/services/ 下；文件格式：key=calssName
     * 
     * @param service
     * @return
     */
    public static <T> Map<String, Class<?>> loadExtensionClasses(Class<T> service) {
        
        ClassLoader cl = SPIExtensionLoader.class.getClassLoader();
        Enumeration<URL> urls = null;
        try {
            urls =  cl.getResources(PREFIX + service.getName());
        } catch (IOException e1) {
            throw new IllegalStateException("Reading file occur error by path "+PREFIX+service.getName(), e1);
        }
        if(null == urls || !urls.hasMoreElements()) {
            return null;
        }
        List<String> list = new ArrayList<>();
        while(true) {
            if(urls.hasMoreElements()) {
                URL url = urls.nextElement();
                InputStream inputStream = null;
                BufferedReader br = null;
                try {
                    inputStream = url.openStream();
                    br = new BufferedReader(new InputStreamReader(inputStream));
                    while(true) {
                        String line = br.readLine();
                        if(StringUtils.isBlank(line)){
                            break;
                        }
                        list.add(line);
                    }
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }finally {
                    try {
                        if(null != br) br.close();
                        if(null != inputStream) inputStream.close();
                    } catch (IOException e) {
                    }
                }
            } else {
                break;
            }
        }
        return list.stream().filter(s -> !StringUtils.startsWith(s, PROPERTY_IGNORE_START_CHAR))
            .map(s -> s.split(PROPERTY_KEY_VALUE_SEPARATOR)).map(a -> KVPair.instance(StringUtils.trim(a[0]), ReflectionUtils.forNameThrowRuntimeExceptionIfNecessary(StringUtils.trim(a[1])) ))
            .collect(Collectors.toMap(KVPair::getK, KVPair::getV));
    }
}
