package ru.combuddy.backend;

import java.util.HashSet;
import java.util.List;

public class Util {
    public static <T> boolean listEqualsIgnoreOrder(List<T> list1, List<T> list2) {
        return new HashSet<>(list1).equals(new HashSet<>(list2));
    }
}
