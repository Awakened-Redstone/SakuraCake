package com.awakenedredstone.sakuracake.internal.registry;

import com.awakenedredstone.sakuracake.internal.registry.util.ReflectionUtil;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
//? if neoforge {
/*import net.neoforged.neoforge.registries.DeferredRegister;
*///?}

import java.lang.reflect.Field;

public interface AutoRegistry<T> extends FieldProcessingSubject<T> {
    Registry<T> registry();

    Class<T> fieldType();

    default void postProcessField(String namespace, T value, String identifier, Field field) {}

    static <T> void init(Class<? extends AutoRegistry<T>> registry) {
        //SakuraCake.LOGGER.debug("Initializing {}", registry.getSimpleName());
        AutoRegistry<T> container = ReflectionUtil.tryInstantiateWithNoArgs(registry);

        if (!registry.isAnnotationPresent(RegistryNamespace.class)) {
            throw new IllegalStateException("Registry class missing @RegistryNamespace annotation");
        }

        if (container.fieldType() == null) {
            throw new NullPointerException("Field type can not be null");
        }

        if (container.registry() == null) {
            throw new NullPointerException("Registry can not be null");
        }

        String namespace = registry.getAnnotation(RegistryNamespace.class).value();

        ReflectionUtil.iterateAccessibleStaticFields(registry, container.fieldType(), createProcessor((fieldValue, identifier, field) -> {
            Registry.register(container.registry(), Identifier.of(namespace, identifier), fieldValue);
            container.postProcessField(namespace, fieldValue, identifier, field);
        }, container));

        container.afterFieldProcessing();
    }

    static <T> ReflectionUtil.FieldConsumer<T> createProcessor(ReflectionUtil.FieldConsumer<T> delegate, FieldProcessingSubject<T> handler) {
        return (value, name, field) -> {
            if (!handler.shouldProcessField(value, name, field)) return;
            delegate.accept(value, name, field);
        };
    }
}
