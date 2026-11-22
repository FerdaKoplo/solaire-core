package dev.jreact.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import dev.jreact.dom.JReact;
import dev.jreact.dom.Renderer;
import dev.jreact.patterns.observer.Subscription;

/**
 * Cache layer:
 *
 *   Query.use("users", fetchUsers)   -> entry (data, error, phase, refetch)
 *   Query.invalidate("users")        -> mark stale + refetch active queries
 *   Query.set("users", mapper)       -> local update without fetch
 *   Query.mutate("users", work, onSuccess)
 *
 * One cache keyed by query key. In-flight requests deduped per key.
 * Stale-while-revalidate: cached data renders immediately, background refetch
 * updates when stale. Subscribers trigger app re-render through JReact.
 */
public final class Query {
    private Query() {}

    public static final long DEFAULT_STALE_MS = 30_000;

    private static final Map<String, CacheEntry> cache = new HashMap<>();
    private static final Set<String> observedKeys = new LinkedHashSet<>();
    private static final Map<String, Fetch<?>> fetchers = new HashMap<>();

    /** Lifecycle of a cached query. */
    public enum Phase { LOADING, SUCCESS, ERROR }

    public static final class Entry<T> {
        private final String key;
        private T data;
        private String error;
        private long fetchedAt;
        private Phase phase;
        private final List<Consumer<Entry<T>>> observers = new ArrayList<>();

        Entry(String key) {
            this.key = key;
            this.phase = Phase.LOADING;
        }

        public T data() { return data; }
        public String error() { return error; }
        public Phase phase() { return phase; }
        public boolean isLoading() { return phase == Phase.LOADING; }
        public boolean hasData() { return data != null; }
        public long fetchedAt() { return fetchedAt; }
        public String key() { return key; }
        public boolean isStale() { return System.currentTimeMillis() - fetchedAt > DEFAULT_STALE_MS; }

        void transition(Phase next) {
            this.phase = next;
            for (Consumer<Entry<T>> o : List.copyOf(observers)) o.accept(this);
            JReact.requestRender();
        }

        public Subscription subscribe(Consumer<Entry<T>> observer) {
            observers.add(observer);
            return () -> observers.remove(observer);
        }
    }

    private static final class CacheEntry {
        Entry<?> entry;
        boolean inFlight;
    }

    /**
     * Read-or-fetch. Call inside a component render: returns current entry and
     * subscribes the component to updates. First call returns phase LOADING;
     * data lands via re-render. Never blocks.
     */
    @SuppressWarnings("unchecked")
    public static <T> Entry<T> use(String key, Fetch<T> fetcher) {
        CacheEntry ce = cache.computeIfAbsent(key, k -> new CacheEntry());
        if (ce.entry == null) ce.entry = new Entry<T>(key);
        Entry<T> entry = (Entry<T>) ce.entry;
        fetchers.put(key, fetcher);
        observedKeys.add(key);

        Renderer.RenderScope scope = Renderer.currentScope();
        if (scope != null) {
            dev.jreact.dom.State<Integer> version = scope.useState(0);
            Runnable sub = () -> version.set(version.get() + 1);
            entry.subscribe(e -> sub.run());
            scope.onCleanup(() -> entry.subscribe(e -> sub.run()).unsubscribe());
        }

        if ((entry.fetchedAt == 0 || entry.isStale()) && !ce.inFlight) fetch(key, fetcher);
        return entry;
    }

    /** Fetch through the cache. Dedupes concurrent calls per key. */
    @SuppressWarnings("unchecked")
    public static <T> void fetch(String key, Fetch<T> fetcher) {
        CacheEntry ce = cache.computeIfAbsent(key, k -> new CacheEntry());
        if (ce.inFlight) return;
        ce.inFlight = true;
        if (ce.entry == null) ce.entry = new Entry<T>(key);
        Entry<T> entry = (Entry<T>) ce.entry;
        entry.error = null;
        entry.transition(Phase.LOADING);
        try {
            fetcher.fetch(value -> {
                CacheEntry live = cache.get(key);
                if (live == null) return;
                live.inFlight = false;
                if (live.entry == null) live.entry = new Entry<T>(key);
                Entry<T> e = (Entry<T>) live.entry;
                e.data = value;
                e.error = null;
                e.fetchedAt = System.currentTimeMillis();
                e.transition(Phase.SUCCESS);
            }, err -> {
                CacheEntry live = cache.get(key);
                if (live == null) return;
                live.inFlight = false;
                if (live.entry == null) live.entry = new Entry<T>(key);
                Entry<T> e = (Entry<T>) live.entry;
                e.error = err == null ? "unknown error" : err;
                e.transition(Phase.ERROR);
            });
        } catch (RuntimeException ex) {
            ce.inFlight = false;
            entry.error = ex.getMessage() == null ? ex.toString() : ex.getMessage();
            entry.transition(Phase.ERROR);
        }
    }

    /** Async fetch contract: call onData exactly once on success, or onError once. */
    @FunctionalInterface
    public interface Fetch<T> {
        void fetch(Consumer<T> onData, Consumer<String> onError);
    }

    /** Adapter for Http.get-style callbacks that take (url, callback). */
    public static <T> Fetch<T> of(java.util.function.BiConsumer<Consumer<T>, Consumer<String>> call) {
        return call::accept;
    }

    /** Mark key(s) stale and refetch. */
    public static void invalidate(String keyPrefix) {
        for (String key : new LinkedHashSet<>(fetchers.keySet())) {
            if (key.startsWith(keyPrefix)) {
                CacheEntry ce = cache.get(key);
                if (ce != null && ce.entry != null) ce.entry.fetchedAt = 0;
                Fetch<?> f = fetchers.get(key);
                if (f != null) fetch(key, f);
            }
        }
    }

    /** Local update without network. */
    @SuppressWarnings("unchecked")
    public static <T> void set(String key, Function<T, T> mapper) {
        CacheEntry ce = cache.get(key);
        if (ce == null || !(ce.entry instanceof Entry)) return;
        Entry<T> e = (Entry<T>) ce.entry;
        e.data = mapper.apply(e.data);
        e.fetchedAt = System.currentTimeMillis();
        e.transition(Phase.SUCCESS);
    }

    /** Mutation: run work, update cache via onSuccess, invalidate key. */
    public static <T> void mutate(String invalidateKey, Fetch<T> work, Consumer<T> onSuccess) {
        work.fetch(value -> {
            if (onSuccess != null) onSuccess.accept(value);
            if (invalidateKey != null) invalidate(invalidateKey);
            else JReact.requestRender();
        }, err -> JReact.requestRender());
    }

    /** Internal: drop cache entries no mounted component observes. */
    public static void gc(Set<String> liveKeys) {
        for (String key : Set.copyOf(observedKeys)) {
            if (!liveKeys.contains(key)) {
                observedKeys.remove(key);
                fetchers.remove(key);
                cache.remove(key);
            }
        }
    }

    /** Force refresh of every cached query. */
    public static void invalidateAll() {
        for (String key : new LinkedHashSet<>(fetchers.keySet())) invalidate(key);
    }
}
