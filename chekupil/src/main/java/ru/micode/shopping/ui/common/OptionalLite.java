package ru.micode.shopping.ui.common;

/**
 * Created by Petr Gusarov on 20.03.18.
 */
public class OptionalLite<T> {

    private final T value;

    private OptionalLite() {
        this.value = null;
    }

    private OptionalLite(T value) {
        if (value == null)
            throw new NullPointerException();
        this.value = value;
    }

    public static <T> OptionalLite<T> ofNullable(T value) {
        return value == null ? new OptionalLite<T>() : of(value);
    }

    public static <T> OptionalLite<T> of(T value) {
        return new OptionalLite<>(value);
    }

    public T orElse(T other) {
        return value != null ? value : other;
    }

    public boolean isPresent() {
        return value != null;
    }

    public void ifPresent(Consumer<? super T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }

    /**
     * Интервейс передачи данных
     *
     * @param <T>
     */
    public interface Consumer<T> {
        void accept(T t);
    }
}
