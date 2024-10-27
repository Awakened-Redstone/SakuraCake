package com.awakenedredstone.sakuracake.internal.registry.util;

import com.awakenedredstone.sakuracake.SakuraCake;
import com.awakenedredstone.sakuracake.internal.registry.AssignedName;
import com.awakenedredstone.sakuracake.internal.registry.IterationIgnored;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Locale;

public class ReflectionUtil {
    public static <C> C tryInstantiateWithNoArgs(Class<C> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException((e instanceof NoSuchMethodException ? "No zero-args constructor defined on class " : "Could not instantiate class ") + clazz, e);
        } /*catch (Throwable e) {
            SakuraCake.LOGGER.error("Stupid registry error", e);
            System.exit(1);
            throw new RuntimeException();
        }*/
    }

    public static <C, F> void iterateAccessibleStaticFields(Class<C> clazz, Class<F> targetFieldType, FieldConsumer<F> fieldConsumer) {
        for (var field : clazz.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) continue;

            F value;
            try {
                value = (F) field.get(null);
            } catch (IllegalAccessException e) {
                continue;
            }

            if (value == null || !targetFieldType.isAssignableFrom(value.getClass())) continue;
            if (field.isAnnotationPresent(IterationIgnored.class)) continue;

            fieldConsumer.accept(value, getFieldName(field), field);
        }
    }

    public static String getFieldName(Field field) {
        var fieldId = field.getName().toLowerCase(Locale.ROOT);
        if (field.isAnnotationPresent(AssignedName.class)) fieldId = field.getAnnotation(AssignedName.class).value();
        return fieldId;
    }

    @FunctionalInterface
    public interface FieldConsumer<F> {
        void accept(F value, String name, Field field);
    }
}
