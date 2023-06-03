package io.github.crucible.omniconfig.lib;

import java.util.function.Consumer;


public class Finalized<T> {
    private T value;

    private Finalized(T value) {
        this.value = value;
    }

    public Finalized<T> set(T value) {
        if (this.value != null) {
            throw new IllegalArgumentException("This Finalized instance was already set!");
        }
        this.value = value;
        return this;
    }

    public T get() {
        return this.value;
    }

    public boolean ifSet(Consumer<T> consumer) {
        if (this.value != null) {
            consumer.accept(this.value);
        }

        return this.value != null;
    }

    public static <T> Finalized<T> empty() {
        return new Finalized<>(null);
    }

    public static <T> Finalized<T> of(T object) {
        return new Finalized<>(object);
    }

}
