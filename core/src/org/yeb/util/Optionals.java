package org.yeb.util;

import java.util.Optional;
import java.util.function.Supplier;

public class Optionals {

    public static <T> Optional<T> or(Optional<T> first, Supplier<Optional<T>> second) {
        return first.isPresent() ? first : second.get();
    }
}
