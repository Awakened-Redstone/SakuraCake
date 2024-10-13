package com.awakenedredstone.sakuracake.internal.registry;

import java.lang.reflect.Field;

public interface FieldProcessingSubject<T> {

    /**
     * @return The class of <b>T</b>
     */
    Class<T> fieldType();

    /**
     * Called to check if a given field should be processed
     *
     * @param value      The value the inspected field currently has
     * @param identifier The identifier that field was assigned, possibly overridden by an {@link AssignedName}
     *                   annotation and always fully lowercase
     * @return {@code true} if the inspected field should be processed
     */
    default boolean shouldProcessField(T value, String identifier, Field field) {
        return true;
    }

    /**
     * Called after all applicable fields of this class have been processed
     */
    default void afterFieldProcessing() {

    }

}
