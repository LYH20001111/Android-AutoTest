package com.newland.autotest.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

public class ReflectionUtils {

    /**
     * 获取某个类中定义 public static final int XX的个数
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
     * @param element 类
     * @param annotationClass 注解类
     * @param memberName 参数名
     * @return
     * @param <T>
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



}