package com.awakenedredstone.sakuracake.util;

import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class PositionStepper<T> implements Iterable<Function<T, T>> {
    private final Function<T, T>[] positions;

    @SafeVarargs
    private PositionStepper(Function<T, T>... positions) {
        this.positions = positions;
    }

    @SafeVarargs
    public static <T> PositionStepper<T> of(Function<T, T>... positions) {
        return new PositionStepper<>(positions);
    }

    public T get(int i, T base) {
        return positions[i].apply(base);
    }

    @Deprecated
    public Function<T, T> getFunction(int i) {
        return positions[i];
    }

    public List<T> getPositions(T base) {
        ArrayList<T> objects = new ArrayList<>(positions.length);
        for (Function<T, T> position : positions) {
            objects.add(position.apply(base));
        }
        return Collections.unmodifiableList(objects);
    }

    public void forEach(T base, Consumer<? super T> consumer) {
        for (Function<T, T> position : positions) {
            consumer.accept(position.apply(base));
        }
    }

    @Override
    public @NotNull Iterator<Function<T, T>> iterator() {
        return new PositionStepperIterator();
    }

    @Override
    public void forEach(Consumer<? super Function<T, T>> action) {
        Iterable.super.forEach(action);
    }

    public class PositionStepperIterator implements Iterator<Function<T, T>> {
        int index = 0;

        @Override
        public boolean hasNext() {
            return index < positions.length - 1;
        }

        @Override
        public Function<T, T> next() {
            return positions[index++];
        }
    }
}
