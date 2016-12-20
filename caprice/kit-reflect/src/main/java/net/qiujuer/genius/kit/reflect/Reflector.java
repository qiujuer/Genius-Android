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

import android.annotation.SuppressLint;
import android.util.ArrayMap;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * This class can use to reflect java class
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class Reflector {


    /**
     * Create a Reflector by class name
     *
     * @param name class full name
     * @return Reflector
     * @throws ReflectException ReflectException
     * @see #with(Class)
     */
    public static Reflector with(String name) throws ReflectException {
        return with(getClassForName(name));
    }

    /**
     * Create a Reflector by class name form ClassLoader
     *
     * @param name        class full name
     * @param classLoader ClassLoader
     * @return Reflector
     * @throws ReflectException ReflectException
     * @see #with(Class)
     */
    public static Reflector with(String name, ClassLoader classLoader) throws ReflectException {
        return with(getClassForName(name, classLoader));
    }

    /**
     * Create a Reflector by class
     *
     * @param clazz Class
     * @return Reflector
     */
    public static Reflector with(Class<?> clazz) {
        return new Reflector(clazz);
    }

    /**
     * Create a Reflector by wrap an obj
     * <p>
     * Use this you can access instance fields
     * and methods with any
     * {@link Object}
     *
     * @param object The obj to be wrapped
     * @return A wrapped obj, to be used for Reflector.
     */
    public static Reflector with(Object object) {
        return new Reflector(object);
    }


    /**
     * Let{@link AccessibleObject} Accessible flag is true
     *
     * @param accessible AccessibleObject
     * @param <T>        AccessibleObject
     * @return AccessibleObject
     */
    public static <T extends AccessibleObject> T accessible(T accessible) {
        if (accessible == null) {
            return null;
        }

        if (accessible instanceof Member) {
            Member member = (Member) accessible;

            // check the modifier is public
            // if the AccessibleObject is can use then return
            if (Modifier.isPublic(member.getModifiers()) &&
                    Modifier.isPublic(member.getDeclaringClass().getModifiers())) {

                return accessible;
            }
        }

        // check the AccessibleObject isAccessible attr
        if (!accessible.isAccessible()) {
            // set accessible flag is true
            accessible.setAccessible(true);
        }

        return accessible;
    }

    // any class
    private final Object obj;
    // the obj is class
    private final boolean isClass;


    private Reflector(Class<?> type) {
        this.obj = type;
        this.isClass = true;
    }

    private Reflector(Object obj) {
        this.obj = obj;
        this.isClass = false;
    }

    /**
     * Get the obj
     *
     * @param <T> Any class
     * @return obj
     */
    @SuppressWarnings("unchecked")
    public <T> T get() {
        return (T) obj;
    }

    /**
     * Set the target field value
     *
     * @param name  field name
     * @param value field value
     * @return Reflector
     * @throws ReflectException
     */
    public Reflector set(String name, Object value) throws ReflectException {
        try {
            Field field = field0(name);
            field.setAccessible(true);
            field.set(obj, unwrap(value));
            return this;
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }

    /**
     * Get the target field value
     *
     * @param name field name
     * @param <T>  The return value type
     * @return field value
     * @throws ReflectException
     */
    @SuppressWarnings("RedundantTypeArguments")
    public <T> T get(String name) throws ReflectException {
        return field(name).<T>get();
    }


    /**
     * Get the target field value, and warp to {@link Reflector}
     *
     * @param name target name
     * @return Reflector
     * @throws ReflectException
     */
    public Reflector field(String name) throws ReflectException {
        try {
            Field field = field0(name);
            return with(field.get(obj));
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }

    private Field field0(String name) throws ReflectException {
        Class<?> type = type();

        try {
            // try get it form public
            return type.getField(name);
        } catch (NoSuchFieldException e) {
            // get declared field
            do {
                try {
                    return accessible(type.getDeclaredField(name));
                } catch (NoSuchFieldException ignore) {
                    // error
                }
                // change to super class
                type = type.getSuperclass();
            }
            while (type != null);
            // if not get it we throw exception
            throw new ReflectException(e);
        }
    }

    /**
     * Get current {@link Reflector#obj} fields by create Map
     *
     * @return a map to Reflector-fields
     */
    public Map<String, Reflector> fields() {
        Map<String, Reflector> result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            result = new ArrayMap<>();
        } else {
            result = new HashMap<>();
        }
        Class<?> type = type();
        do {
            // all field
            for (Field field : type.getDeclaredFields()) {
                if (!isClass ^ Modifier.isStatic(field.getModifiers())) {
                    String name = field.getName();
                    // add
                    if (!result.containsKey(name))
                        result.put(name, field(name));
                }
            }

            type = type.getSuperclass();
        }
        while (type != null);

        return result;
    }

    /**
     * Call current {@link Reflector#obj} method
     *
     * @param name method name
     * @return method return value is contained in {@link Reflector}
     * @throws ReflectException
     */
    public Reflector call(String name) throws ReflectException {
        return call(name, new Object[0]);
    }


    /**
     * Call current {@link Reflector#obj} method
     *
     * @param name method name
     * @param args method parameters
     * @return method return value is contained in {@link Reflector}
     * @throws ReflectException
     */
    public Reflector call(String name, Object... args) throws ReflectException {
        Class<?>[] types = getTypes(args);

        try {
            Method method = exactMethod(name, types);
            return with(method, obj, args);
        } catch (NoSuchMethodException e) {
            try {
                Method method = similarMethod(name, types);
                return with(method, obj, args);
            } catch (NoSuchMethodException e1) {
                throw new ReflectException(e1);
            }
        }
    }


    private Method exactMethod(String name, Class<?>[] types) throws NoSuchMethodException {
        Class<?> type = type();

        try {
            return type.getMethod(name, types);
        } catch (NoSuchMethodException e) {
            do {
                try {
                    return type.getDeclaredMethod(name, types);
                } catch (NoSuchMethodException ignore) {
                }

                type = type.getSuperclass();
            }
            while (type != null);

            throw new NoSuchMethodException();
        }
    }

    private Method similarMethod(String name, Class<?>[] types) throws NoSuchMethodException {
        Class<?> type = type();

        for (Method method : type.getMethods()) {
            if (isSimilarSignature(method, name, types)) {
                return method;
            }
        }

        do {
            for (Method method : type.getDeclaredMethods()) {
                if (isSimilarSignature(method, name, types)) {
                    return method;
                }
            }

            type = type.getSuperclass();
        }
        while (type != null);

        throw new NoSuchMethodException("No similar method " + name + " with params " + Arrays.toString(types) + " could be found with type " + type() + ".");
    }

    private boolean isSimilarSignature(Method possiblyMatchingMethod, String desiredMethodName, Class<?>[] desiredParamTypes) {
        return possiblyMatchingMethod.getName().equals(desiredMethodName) && match(possiblyMatchingMethod.getParameterTypes(), desiredParamTypes);
    }


    /**
     * We can create class by none structural parameters
     *
     * @return new none structural parameters class
     * @throws ReflectException
     */
    public Reflector create() throws ReflectException {
        return create(new Object[0]);
    }

    /**
     * We can create class by args structural parameters
     *
     * @param args structural parameters
     * @return new structural parameters class
     * @throws ReflectException
     */
    public Reflector create(Object... args) throws ReflectException {
        Class<?>[] types = getTypes(args);

        try {
            Constructor<?> constructor = type().getDeclaredConstructor(types);
            return with(constructor, args);
        } catch (NoSuchMethodException e) {
            for (Constructor<?> constructor : type().getDeclaredConstructors()) {
                if (match(constructor.getParameterTypes(), types)) {
                    return with(constructor, args);
                }
            }

            throw new ReflectException(e);
        }
    }

    /**
     * Create a dynamic proxy
     * if invoke error try get value by Map when {@link Reflector#obj} instanceof Map
     *
     * @param proxyType proxy type
     * @param <P>       proxy class
     * @return proxy class
     */
    @SuppressWarnings("unchecked")
    public <P> P proxy(Class<P> proxyType) {
        final boolean isMap = (obj instanceof Map);
        final InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String name = method.getName();
                try {
                    return with(obj).call(name, args).get();
                } catch (ReflectException e) {
                    if (isMap) {
                        Map<String, Object> map = (Map<String, Object>) obj;
                        int length = (args == null ? 0 : args.length);

                        if (length == 0 && name.startsWith("get")) {
                            return map.get(toLowerCaseFirstOne(name.substring(3)));
                        } else if (length == 0 && name.startsWith("is")) {
                            return map.get(toLowerCaseFirstOne(name.substring(2)));
                        } else if (length == 1 && name.startsWith("set")) {
                            map.put(toLowerCaseFirstOne(name.substring(3)), args[0]);
                            return null;
                        }
                    }

                    throw e;
                }
            }
        };

        return (P) Proxy.newProxyInstance(proxyType.getClassLoader(), new Class[]{proxyType}, handler);
    }


    /**
     * Change string first char to lower case
     *
     * @param string wait change string
     * @return new first lower case string
     */
    @SuppressLint("DefaultLocale")
    private static String toLowerCaseFirstOne(String string) {
        int length = string.length();

        if (length == 0 || Character.isLowerCase(string.charAt(0))) {
            return string;
        } else if (length == 1) {
            return string.toLowerCase();
        } else {
            return (new StringBuilder())
                    .append(Character.toLowerCase(string.charAt(0)))
                    .append(string.substring(1)).toString();
        }
    }

    /**
     * match two methods
     *
     * @param declaredTypes declared method types
     * @param actualTypes   actual method types
     * @return is match
     */
    private boolean match(Class<?>[] declaredTypes, Class<?>[] actualTypes) {
        if (declaredTypes.length == actualTypes.length) {
            for (int i = 0; i < actualTypes.length; i++) {
                if (actualTypes[i] == NULL.class)
                    continue;

                // we can get method real type
                if (realType(declaredTypes[i]).isAssignableFrom(realType(actualTypes[i])))
                    continue;

                return false;
            }

            return true;
        } else {
            return false;
        }
    }

    private static Reflector with(Constructor<?> constructor, Object... args) throws ReflectException {
        try {
            return with(accessible(constructor).newInstance(args));
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }

    private static Reflector with(Method method, Object object, Object... args) throws ReflectException {
        try {
            accessible(method);

            if (method.getReturnType() == void.class) {
                method.invoke(object, args);
                return with(object);
            } else {
                return with(method.invoke(object, args));
            }
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }


    /**
     * Un wrap the obj
     * if obj is {@link Reflector} type, we can call Reflector.get()
     *
     * @param object Object
     * @return real obj
     */
    private static Object unwrap(Object object) {
        if (object instanceof Reflector) {
            return ((Reflector) object).get();
        }

        return object;
    }


    /**
     * Get objects type
     * if obj is null, we return {@link NULL} class
     *
     * @param objects Object array
     * @return obj type array
     * @see Object#getClass()
     */
    private static Class<?>[] getTypes(Object... objects) {
        if (objects == null) {
            return new Class[0];
        }

        Class<?>[] result = new Class[objects.length];

        for (int i = 0; i < objects.length; i++) {
            Object value = objects[i];
            result[i] = value == null ? NULL.class : value.getClass();
        }

        return result;
    }

    /**
     * Build {@link Class} by name
     * the build opt have init class static region
     *
     * @param name class full name
     * @return {@link Class}
     * @throws ReflectException
     * @see Class#forName(String)
     */
    private static Class<?> getClassForName(String name) throws ReflectException {
        try {
            return Class.forName(name);
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }

    /**
     * Build {@link Class} by name
     * form the {@link ClassLoader}
     * the build opt have init class static region
     *
     * @param name        class full name
     * @param classLoader ClassLoader
     * @return {@link Class}
     * @throws ReflectException
     * @see Class#forName(String)
     */
    private static Class<?> getClassForName(String name, ClassLoader classLoader) throws ReflectException {
        try {
            return Class.forName(name, true, classLoader);
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }

    /**
     * Check the type, if type is base type then return base type class
     * but isn't we return the type
     *
     * @param type wait check type
     * @return real type
     */
    private static Class<?> realType(Class<?> type) {
        if (type == null) {
            return null;
        } else if (type.isPrimitive()) {
            if (boolean.class == type) {
                return Boolean.class;
            } else if (int.class == type) {
                return Integer.class;
            } else if (long.class == type) {
                return Long.class;
            } else if (short.class == type) {
                return Short.class;
            } else if (byte.class == type) {
                return Byte.class;
            } else if (double.class == type) {
                return Double.class;
            } else if (float.class == type) {
                return Float.class;
            } else if (char.class == type) {
                return Character.class;
            } else if (void.class == type) {
                return Void.class;
            }
        }
        return type;
    }


    /**
     * Get now {@link Reflector} obj class type
     *
     * @return Class
     * @see Object#getClass()
     */
    public Class<?> type() {
        if (isClass) {
            return (Class<?>) obj;
        } else {
            return obj.getClass();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return obj.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Reflector
                && this.obj.equals(((Reflector) obj).get());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return obj.toString();
    }

    /**
     * This is NULL class support null obj
     */
    public static class NULL {
        // none do
    }

    /**
     * Get the underlying class for a type, or null if the type is a variable
     * type.
     *
     * @param type the type
     * @return the underlying class
     */
    public static Class<?> getClass(Type type) {
        if (type instanceof Class) {
            return (Class) type;
        } else if (type instanceof ParameterizedType) {
            return getClass(((ParameterizedType) type).getRawType());
        } else if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            Class<?> componentClass = getClass(componentType);
            if (componentClass != null) {
                return Array.newInstance(componentClass, 0).getClass();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Get the underlying class array for a type array, or null if the type is a variable
     * type.
     *
     * @param types the type array
     * @return a class array of the raw classes for actual the type arguments.
     */
    public static <T> Class<?>[] getClasses(Type[] types) {
        if (types == null)
            return null;
        if (types.length == 0)
            return new Class<?>[0];

        Class<?>[] classes = new Class[types.length];
        // Resolve types by chasing down type variables.
        for (int i = 0; i < types.length; i++) {
            classes[i] = getClass(types[i]);
        }
        return classes;
    }


    /**
     * Get the actual type arguments a child class has used to extend a generic
     * base class.
     * <p>
     * Child class must be the implementation class of base class.
     * Base class must be a generic class.
     *
     * @param baseClass  the base class
     * @param childClass the child class
     * @return a array type of the raw classes for the actual type arguments.
     */
    public static <T> Type[] getActualTypeArguments(
            final Class<T> baseClass, final Class<?> childClass) {

        // Create map
        Map<Type, Type> resolvedTypes;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            resolvedTypes = new ArrayMap<>();
        } else {
            resolvedTypes = new HashMap<>();
        }

        Type type = childClass;
        // start walking up the inheritance hierarchy until we hit baseClass
        while (!getClass(type).equals(baseClass)) {
            if (type instanceof Class) {
                // there is no useful information for us in raw types, so just keep going.
                type = ((Class) type).getGenericSuperclass();
            } else {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Class<?> rawType = (Class) parameterizedType.getRawType();

                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
                for (int i = 0; i < actualTypeArguments.length; i++) {
                    resolvedTypes.put(typeParameters[i], actualTypeArguments[i]);
                }

                if (!rawType.equals(baseClass)) {
                    type = rawType.getGenericSuperclass();
                }
            }
        }

        // finally, for each actual type argument provided to baseClass, determine (if possible)
        // the raw class for that type argument.
        Type[] actualTypeArguments;
        if (type instanceof Class) {
            actualTypeArguments = ((Class) type).getTypeParameters();
        } else {
            actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
        }
        // resolve types by chasing down type variables.
        for (int i = 0; i < actualTypeArguments.length; i++) {
            Type tempType = actualTypeArguments[i];

            // First search really type
            while (resolvedTypes.containsKey(tempType)) {
                tempType = resolvedTypes.get(tempType);
            }

            // If the type instanceof ParameterizedType,
            // we need replace types by getActualTypeArguments()
            tempType = replaceTypeActualArgument(tempType, resolvedTypes);

            actualTypeArguments[i] = tempType;
        }
        return actualTypeArguments;
    }

    /**
     * Replace {@link ParameterizedType#getActualTypeArguments()} method return value.
     * In this we use {@link ParameterizeTypeActualArgsDelegate} delegate {@link ParameterizedType};
     * Let {@link ParameterizedType#getActualTypeArguments()} return really class type.
     *
     * @param inType        Type
     * @param resolvedTypes a Map<Type, Type>, {@link #getActualTypeArguments(Class, Class)}
     * @return {@link ParameterizeTypeActualArgsDelegate}
     */
    private static Type replaceTypeActualArgument(Type inType, final Map<Type, Type> resolvedTypes) {
        Type outType = inType;

        if (inType instanceof ParameterizedType) {
            final ParameterizedType finalType = ((ParameterizedType) inType);
            final Type[] actualArgs = ((ParameterizedType) inType).getActualTypeArguments();

            for (int i = 0; i < actualArgs.length; i++) {
                Type argType = actualArgs[i];
                while (resolvedTypes.containsKey(argType)) {
                    argType = resolvedTypes.get(argType);
                }

                // Do replace ActualArgument
                argType = replaceTypeActualArgument(argType, resolvedTypes);

                actualArgs[i] = argType;
            }

            outType = new ParameterizeTypeActualArgsDelegate(finalType, actualArgs);
        }
        return outType;
    }

}
