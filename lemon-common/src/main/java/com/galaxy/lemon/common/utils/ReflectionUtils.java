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

package com.galaxy.lemon.common.utils;

import org.springframework.util.ClassUtils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * 反射辅助类
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class ReflectionUtils extends org.springframework.util.ReflectionUtils {

    protected static final String[] PRIMITIVE_NAMES = new String[] { "boolean",
        "byte", "char", "double", "float", "int", "long", "short", "void" };

    protected static final Class<?>[] PRIMITIVES = new Class[] { boolean.class,
        byte.class, char.class, double.class, float.class, int.class,
        long.class, short.class, Void.TYPE };
    
    private static final Map<Class<?>, Field[]> declaredFieldsCache = new ConcurrentHashMap<>(256);
    private static final Map<AllFieldsCacheKey, Field[]> allFieldCache = Collections.synchronizedMap(new WeakHashMap<AllFieldsCacheKey, Field[]>());


    /**
     * Return the default ClassLoader to use: typically the thread context
     * ClassLoader, if available; the ClassLoader that loaded the ClassUtils
     * class will be used as fallback.
     * <p>Call this method if you intend to use the thread context ClassLoader
     * in a scenario where you clearly prefer a non-null ClassLoader reference:
     * for example, for class path resource loading (but not necessarily for
     * {@code Class.forName}, which accepts a {@code null} ClassLoader
     * reference as well).
     * @return the default ClassLoader (only {@code null} if even the system
     * ClassLoader isn't accessible)
     * @see Thread#getContextClassLoader()
     * @see ClassLoader#getSystemClassLoader()
     */
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        }
        catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = ReflectionUtils.class.getClassLoader();
            if (cl == null) {
                // getClassLoader() returning null indicates the bootstrap ClassLoader
                try {
                    cl = ClassLoader.getSystemClassLoader();
                }
                catch (Throwable ex) {
                    // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                }
            }
        }
        return cl;
    }

    /**
     * 根据类获取所有的Fields
     * @param clazz
     * @return
     */
    public static <T> Field[] getDeclaredFields(Class<T> clazz){
        if (null == clazz) {
            return new Field[]{};
        }
        Field[] fields = declaredFieldsCache.get(clazz);
        if (null == fields) {
            fields = clazz.getDeclaredFields();
            declaredFieldsCache.put(clazz, fields);
        }
        return Optional.ofNullable(fields).orElseGet(() -> new Field[]{});
    }

    /**
     * get field value
     * @param field
     * @param object
     * @return
     */
    public static Object getFieldValue(Field field , Object object){
        makeAccessible(field);
        try {
            return field.get(object);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            handleReflectionException(e);
        }
        throw new IllegalStateException("Should never get here");
    }

    /**
     * set field value
     * @param field
     * @param object
     * @param value
     */
    public static void setFieldValue(Field field , Object object , Object value){
        makeAccessible(field);
        try {
            field.set(object, value);
        } catch (IllegalArgumentException e){
            try {
                field.set(object, convertType(field.getType(), value));
            } catch (IllegalArgumentException | IllegalAccessException e1) {
                handleReflectionException(e);
            }
        } catch (IllegalAccessException e) {
            handleReflectionException(e);
        }
    }

    /**
     * 获取 {@code clazz} 及其父类的Field, 父类查询截止到{@code endSuperClass}
     * @param clazz
     * @return
     */
    public static Field[] getAllFields(Class<?> clazz, Class<?> endSuperClass) {
        AllFieldsCacheKey allFieldsCacheKey = new AllFieldsCacheKey(clazz, endSuperClass);
        if (allFieldCache.get(allFieldsCacheKey) != null) {
            return allFieldCache.get(allFieldsCacheKey);
        }
        synchronized (allFieldCache) {
            if (allFieldCache.get(allFieldsCacheKey) != null) {
                return allFieldCache.get(allFieldsCacheKey);
            }
            Class<?> parsedClass = clazz;
            List<Field> fieldList = new ArrayList<>();
            while (true) {
                Stream.of(parsedClass.getDeclaredFields()).forEach(fieldList::add);
                parsedClass = parsedClass.getSuperclass();
                if (parsedClass == Object.class || (null != endSuperClass && parsedClass.equals(endSuperClass))) {
                    break;
                }
            }
            allFieldCache.put(allFieldsCacheKey, fieldList.toArray(new Field[fieldList.size()]));
        }

        return allFieldCache.get(allFieldsCacheKey);
    }

    /**
     * 获取 {@code clazz} 及其父类的Field
     * @param clazz
     * @return
     */
    public static Field[] getAllFields(Class<?> clazz) {
        return getAllFields(clazz, null);
    }

    /**
     * cast
     * @param returnType
     * @param value
     * @return
     */
    public static Object convertType(Class<?> returnType , Object value){
        if (returnType == null || value == null) {
            return null;
        } else if (returnType == String.class) {
            return String.valueOf(value);
        } else if (returnType == boolean.class || returnType == Boolean.class) {
            return Boolean.valueOf(String.valueOf(value));
        } else if (returnType == char.class || returnType == Character.class) {
            return Character.valueOf((char)value);
        } else if (returnType == byte.class || returnType == Byte.class) {
            return Byte.valueOf(String.valueOf(value));
        } else if (returnType == short.class || returnType == Short.class) {
            return Short.valueOf(String.valueOf(value));
        } else if (returnType == int.class || returnType == Integer.class) {
            return Integer.valueOf(String.valueOf(value));
        } else if (returnType == long.class || returnType == Long.class) {
            return Long.valueOf(String.valueOf(value));
        } else if (returnType == float.class || returnType == Float.class) {
            return Float.valueOf(String.valueOf(value));
        } else if (returnType == double.class || returnType == Double.class) {
            return Double.valueOf(String.valueOf(value));
        } else if (returnType == BigDecimal.class) {
            return new BigDecimal(value.toString());
        } else if (returnType == LocalDate.class) {
            return DateTimeUtils.parseLocalDate(value.toString());
        } else if (returnType == LocalDateTime.class) {
            return DateTimeUtils.parseLocalDateTime(value.toString());
        }
        return value;
    }
    
    /**
     * Invoke the specified {@link Method} against the supplied target object with no arguments.
     * The target object can be {@code null} when invoking a static {@link Method}.
     * <p>Thrown exceptions are handled via a call to {@link #handleReflectionException}.
     * @param method the method to invoke
     * @param target the target object to invoke the method on
     * @return the invocation result, if any
     * @see #invokeMethod(java.lang.reflect.Method, Object, Object[])
     */
    public static Object invokeMethod(Method method, Object target) {
        return invokeMethod(method, target, new Object[0]);
    }

    /**
     * Invoke the specified {@link Method} against the supplied target object with the
     * supplied arguments. The target object can be {@code null} when invoking a
     * static {@link Method}.
     * <p>Thrown exceptions are handled via a call to {@link #handleReflectionException}.
     * @param method the method to invoke
     * @param target the target object to invoke the method on
     * @param args the invocation arguments (may be {@code null})
     * @return the invocation result, if any
     */
    public static Object invokeMethod(Method method, Object target, Object... args) {
        try {
            return method.invoke(target, args);
        }
        catch (Exception ex) {
            handleReflectionException(ex);
        }
        throw new IllegalStateException("Should never get here");
    }
    
    /**
     * 获取类的属性的读方法（属性名，方法）
     * @param clazz
     * @return
     */
    public static <T> Map<String,Method> getDeclareReadMethods(Class<T> clazz) {
        Map<String,Method> readMethods = new HashMap<>();
        Arrays.asList(getPropertyDescriptors(clazz)).forEach(pd -> {
            Method readMethod = pd.getReadMethod();
            makeAccessible(readMethod);
            readMethods.put(pd.getName(), readMethod);
            });
        return readMethods;
    }

    /**
     * 获取类的属性的读方法（属性名，方法），包含非public的方法
     *
     * @param clazz
     * @return
     */
    public static <T> Map<String,Method> getAllDeclareReadMethods(Class<T> clazz){
        Map<String, Method> readMethods = new HashMap<>();
        Arrays.asList(getPropertyDescriptors(clazz)).stream().forEach(pd -> {
            Method readMethod = pd.getReadMethod();
            if(null == readMethod) {
                readMethod = getDeclaredMethodByName(clazz, "get"+ StringUtils.capitalize(pd.getName()));
            }
            if (null != readMethod) {
                makeAccessible(readMethod);
                readMethods.put(pd.getName(), readMethod);
            }
        });
        return readMethods;
    }

    /**
     * 获取类的属性的读方法（属性名，方法）
     * @param clazz
     * @return
     */
    public static <T> Map<String,Method> getDeclareWriteMethods(Class<T> clazz){
        Map<String,Method> writeMethods = new HashMap<>();
        Arrays.asList(getPropertyDescriptors(clazz)).stream().forEach(pd -> {
            Method writeMethod = pd.getWriteMethod();
            makeAccessible(writeMethod);
            writeMethods.put(pd.getName(), writeMethod);
        });
        return writeMethods;
    }
    
    /**
     * 获取类的属性的写方法（属性名，方法），包含非public的方法
     * 
     * @param clazz
     * @return
     */
    public static <T> Map<String,Method> getAllDeclareWriteMethods(Class<T> clazz){
        Map<String, Method> writeMethods = new HashMap<>();
        Arrays.asList(getPropertyDescriptors(clazz)).stream().forEach(pd -> {
            Method writeMethod = pd.getWriteMethod();
            if(null == writeMethod) {
                writeMethod = getDeclaredMethodByName(clazz, "set"+ StringUtils.capitalize(pd.getName()));
            }
            if (null != writeMethod) {
                makeAccessible(writeMethod);
                writeMethods.put(pd.getName(), writeMethod);
            }
        });
        return writeMethods;
    }

    /**
     * 获取write method
     * @param pd
     * @param clazz
     * @return
     */
    public static Method getWriteMethod(PropertyDescriptor pd, Class<?> clazz) {
        Method writeMethod = pd.getWriteMethod();
        if(null == writeMethod) {
            writeMethod = getDeclaredMethodByName(clazz, "set"+ StringUtils.capitalize(pd.getName()));
        }
        return writeMethod;
    }

    /**
     * 自省属性描述
     * @param clazz
     * @return
     */
    public static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) {
        try {
            return Introspector.getBeanInfo(clazz).getPropertyDescriptors();
        } catch (IntrospectionException e) {
            throw new IllegalStateException("Could not get bean info for class " + clazz, e);
        }
    }

    /**
     * 根据方法名获取Method
     * @param clazz
     * @param methodName
     * @return null is not exists
     */
    public static Method getDeclaredMethodByName(final Class<?> clazz, final String methodName) {
        Method[] methods = clazz.getDeclaredMethods();
        Method result = null;
        int count = 0;
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                result = method;
                count++;
            }
        }
        if (count > 1) {
            throw new IllegalStateException("Too many method with name \"" + methodName + "\" exists.");
        }
        return result;
    }
    
    /**
     * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问.
     * 如向上转型到Object仍无法找到, 返回null.
     * 只匹配函数名。
     * 
     * 用于方法需要被多次调用的情况. 先使用本函数先取得Method,然后调用Method.invoke(Object obj, Object... args)
     */
    public static Method getAccessibleMethodByName(final Object obj, final String methodName) {
        Method result = null;
        int count = 0;
        for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType.getSuperclass()) {
            Method[] methods = searchType.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    makeAccessible(method);
                    result =  method;
                    count++;
                }
            }
        }
        if (count > 1) {
            throw new IllegalStateException("Too many method with name \"" + methodName + "\" exists.");
        }
        return result;
    }
    
    /**
     * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问.
     * 如向上转型到Object仍无法找到, 返回null.
     * 只匹配函数名。
     * 
     * 用于方法需要被多次调用的情况. 先使用本函数先取得Method,然后调用Method.invoke(Object obj, Object... args)
     */
    public static Method getAccessibleMethodByName(final Class<?> clazz, final String methodName) {
        Method result = null;
        int count = 0;
        for (Class<?> searchType = clazz; searchType != Object.class; searchType = searchType.getSuperclass()) {
            Method[] methods = searchType.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    makeAccessible(method);
                    result = method;
                }
            }
        }
        if (count > 1) {
            throw new IllegalStateException("Too many method with name \"" + methodName + "\" exists.");
        }
        return result;
    }
    
    /**
     * 根据field name 查找set method
     * @param clazz
     * @param field
     * @return
     */
    public static Method getAccessibleWriteMethodByField(final Class<?> clazz, final Field field) {
        String fieldNm = field.getName();
        String methodName = "set"+fieldNm.substring(0, 1).toUpperCase()+fieldNm.substring(1);
        return getAccessibleMethodByName(clazz, methodName);
    }
    
    /**
     *  获取类的泛型
     * @param cls
     * @return
     */
    public static Class<?> getGenericClass(Class<?> cls) {
        return getGenericClass(cls, 0);
    }

    /**
     * 获取类的泛型
     * @param cls
     * @param i
     * @return
     */
    public static Class<?> getGenericClass(Class<?> cls, int i) {
        try {
            ParameterizedType parameterizedType = ((ParameterizedType) cls.getGenericInterfaces()[0]);
            Object genericClass = parameterizedType.getActualTypeArguments()[i];
            if (genericClass instanceof ParameterizedType) { // 处理多级泛型
                return (Class<?>) ((ParameterizedType) genericClass).getRawType();
            } else if (genericClass instanceof GenericArrayType) { // 处理数组泛型
                return (Class<?>) ((GenericArrayType) genericClass).getGenericComponentType();
            } else if (genericClass != null) {
                return (Class<?>) genericClass;
            }
        } catch (Throwable e) {
        }
        if (cls.getSuperclass() != null) {
            return getGenericClass(cls.getSuperclass(), i);
        } else {
            throw new IllegalArgumentException(cls.getName() + " generic type undefined!");
        }
    }
    
    /**
     * 获取feild的类的泛型
     * @param field
     * @return
     */
    public static Class<?> getGenericClass(Field field) {
        return getGenericClass(field,0);
    }

    /**
     * 获取feild的类的泛型
     * @param field
     * @param i 第几个泛型参数
     * @return
     */
    public static Class<?> getGenericClass(Field field, int i) {
        ParameterizedType parameterizedType = (ParameterizedType)field.getGenericType();
        return Optional.ofNullable(parameterizedType.getActualTypeArguments()[i]).map(g -> (Class<?>) g).orElseThrow(() -> new IllegalArgumentException(field + " generic type undefined!"));
    }

    /**
     * new instance
     * @param clazz class
     * @param <T>
     * @return
     */
    public static <T> T newInstance(Class<T> clazz){
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Can't new instance for "+clazz+" :" + e.getMessage());
        }
    }

    /**
     * field 是否为List类型
     * @param field
     * @return
     */
    public static boolean isList(Field field){
        return List.class.isAssignableFrom(field.getType());
    }
    
    /**
     * 是否有默认构造函数
     * @param clazz
     * @return
     */
    public static boolean hasDefaultConstructor(Class<?> clazz) { 
        try {
            clazz.getConstructor(new Class<?>[]{});
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        } catch (SecurityException e) {
            throw e;
        }
    }
    
    public static boolean isPublic(Class<?> clazz) {
        return Modifier.isPublic(clazz.getModifiers());
    }
    
    /**
     * 是否简单类型
     * @param clazz
     * @return
     */
    public static boolean isPrimitive(Class<?> clazz) {
        return clazz.isPrimitive() || clazz == String.class || clazz == Boolean.class || clazz == Character.class
                || Number.class.isAssignableFrom(clazz) || Date.class.isAssignableFrom(clazz);
    }
    
    /**
     * ClassNotFoundException convert to RuntimeException
     * @param name
     * @return
     */
    public static Class<?> forNameThrowRuntimeExceptionIfNecessary(String name) {
        try {
            return forName(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * use default class loader
     * @param name
     * @return
     * @throws ClassNotFoundException
     */
    public static Class<?> forName(String name) throws ClassNotFoundException {
        return forName(name, getDefaultClassLoader());
    }

    /**
     *
     * @param name
     * @param classLoader
     * @return
     * @throws ClassNotFoundException
     */
    public static Class<?> forName(String name, ClassLoader classLoader) throws ClassNotFoundException {
        if (null == name || "".equals(name)) {
            return null;
        }
        Class<?> c = forNamePrimitive(name);
        if (c == null) {
            if (name.endsWith("[]")) {
                String nc = name.substring(0, name.length() - 2);
                try {
                    c = Class.forName(nc, true, classLoader);
                } catch (ClassNotFoundException e) {
                    throw new IllegalStateException(e);
                }
                c = Array.newInstance(c, 0).getClass();
            } else {
                c = Class.forName(name, true, classLoader);
            }
        }
        return c;
    }

    /**
     * primitive class
     * @param name
     * @return
     */
    protected static Class<?> forNamePrimitive(String name) {
        if (name.length() <= 8) {
            int p = Arrays.binarySearch(PRIMITIVE_NAMES, name);
            if (p >= 0) {
                return PRIMITIVES[p];
            }
        }
        return null;
    }

    /**
     * class named of className is present
     * @param className
     * @return
     */
    public static boolean isPresent(String className) {
        return ClassUtils.isPresent(className, ReflectionUtils.class.getClassLoader());
    }

    static class AllFieldsCacheKey {
        private Class<?> startClass;
        private Class<?> endClass;

        public AllFieldsCacheKey(Class<?> startClass, Class<?> endClass) {
            this.startClass = startClass;
            this.endClass = endClass;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AllFieldsCacheKey that = (AllFieldsCacheKey) o;

            if (startClass != null ? !startClass.equals(that.startClass) : that.startClass != null) return false;
            return endClass != null ? endClass.equals(that.endClass) : that.endClass == null;
        }

        @Override
        public int hashCode() {
            int result = startClass != null ? startClass.hashCode() : 0;
            result = 31 * result + (endClass != null ? endClass.hashCode() : 0);
            return result;
        }
    }

}
