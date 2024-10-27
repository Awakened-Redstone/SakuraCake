package com.awakenedredstone.sakuracake.util;

import java.util.List;

/**
 * The most cursed class you find on this mod, yes, I know, this is horrible
 * and this shouldn't exist, but casts can be long and annoying, this also makes IDEA quiet
*/
public class Cast {
    public static <T, L extends List<T>> L deExtendList(List<? extends T> value) {
        return (L) value;
    }

    public static <T> Class<T> conform(Class<?> input) {
        return (Class<T>) input;
    }
}
