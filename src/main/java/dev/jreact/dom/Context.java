package dev.jreact.dom;

/**
 * Context token for dependency injection down the tree. A {@code Provider}
 * publishes a value; descendants read it via {@code scope.useContext(ctx)}.
 *
 *   Context<Theme> Theme = Context.of(() -> Themes.light());
 *   ...
 *   El.provider(Theme, Themes.dark(), () -> AppShell.appShell(scope, ...));
 *   ...
 *   Theme theme = scope.useContext(Theme);
 */
public final class Context<T> {
    private final java.util.function.Supplier<T> fallback;

    private Context(java.util.function.Supplier<T> fallback) {
        this.fallback = fallback;
    }

    /** Value when no Provider above supplies one. */
    public T defaultValue() { return fallback.get(); }

    public static <T> Context<T> of(T defaultValue) { return new Context<>(() -> defaultValue); }

    public static <T> Context<T> of(java.util.function.Supplier<T> fallback) { return new Context<>(fallback); }
}