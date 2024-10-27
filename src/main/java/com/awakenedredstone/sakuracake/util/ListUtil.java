package com.awakenedredstone.sakuracake.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListUtil {
    /**
     * Appends a single item to an immutable list
     */
    public static <T> List<T> append(List<T> list, T item) {
        List<T> tmpList = new ArrayList<>(list);
        tmpList.add(item);
        return Collections.unmodifiableList(tmpList);
    }
}
