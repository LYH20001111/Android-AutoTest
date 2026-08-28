package com.hudou.autotest.util;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectionUtils {

    /**
     * 注解值缓存。Class/Method 均有稳定的 equals/hashCode，可安全作为缓存键组成部分。
     * 值为 {@link #NULL_MARKER} 时表示已解析但结果为 null，避免重复反射。
     */
    private static final ConcurrentHashMap<AnnotationCacheKey, String> ANNOTATION_VALUE_CACHE =
            new ConcurrentHashMap<>();
    private static final String NULL_MARKER = "\u0000ANNOTATION_NULL\u0000";

    private static volatile Properties configCache;

    /**
     * 获取某个类中定义 public static final int XX的个数
     *
     * @param clazz 类.class
     * @return int 个数
     */
    public static int countPublicStaticFinalInts(Class<?> clazz) {
        int count = 0;
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            if (Modifier.isPublic(field.getModifiers()) &&
                    Modifier.isStatic(field.getModifiers()) &&
                    Modifier.isFinal(field.getModifiers()) &&
                    field.getType() == int.class) {
                count++;
            }
        }
        return count;
    }

    /**
     * 获取某个类定义某注解的某个参数值
     *
     * @param element         类
     * @param annotationClass 注解类
     * @param memberName      参数名
     * @param <T>
     * @return
     */
    public static <T extends AnnotatedElement> String getAnnotationValue(
            T element, Class<? extends Annotation> annotationClass, String memberName) {
        if (element.isAnnotationPresent(annotationClass)) {
            Annotation annotation = element.getAnnotation(annotationClass);
            try {
                return (String) annotation.annotationType().getMethod(memberName).invoke(annotation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 带缓存的注解参数值读取，结果与 {@link #getAnnotationValue} 完全一致。
     * 同一 element + 注解 + 参数名只反射一次，热路径（逐用例/逐测试项读取）应使用本方法。
     */
    public static <T extends AnnotatedElement> String getAnnotationValueCached(
            T element, Class<? extends Annotation> annotationClass, String memberName) {
        AnnotationCacheKey key = new AnnotationCacheKey(element, annotationClass, memberName);
        String cached = ANNOTATION_VALUE_CACHE.get(key);
        if (cached != null) {
            return NULL_MARKER.equals(cached) ? null : cached;
        }
        String value = getAnnotationValue(element, annotationClass, memberName);
        ANNOTATION_VALUE_CACHE.put(key, value == null ? NULL_MARKER : value);
        return value;
    }

    public static <T> T createInstance(Class<T> clz) {
        try {
            Constructor<T> constructor = clz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
                 InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getConfig(String key) {
        Properties properties = configCache;
        if (properties == null) {
            synchronized (ReflectionUtils.class) {
                properties = configCache;
                if (properties == null) {
                    properties = new Properties();
                    try {
                        InputStream inputStream = ReflectionUtils.class.getClassLoader().getResourceAsStream("config.properties");
                        properties.load(inputStream);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    configCache = properties;
                }
            }
        }
        return properties.getProperty(key);
    }

    private static final class AnnotationCacheKey {
        private final AnnotatedElement element;
        private final Class<? extends Annotation> annotationClass;
        private final String memberName;

        private AnnotationCacheKey(AnnotatedElement element,
                                   Class<? extends Annotation> annotationClass,
                                   String memberName) {
            this.element = element;
            this.annotationClass = annotationClass;
            this.memberName = memberName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AnnotationCacheKey)) return false;
            AnnotationCacheKey that = (AnnotationCacheKey) o;
            return element.equals(that.element)
                    && annotationClass.equals(that.annotationClass)
                    && memberName.equals(that.memberName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(element, annotationClass, memberName);
        }
    }
}
