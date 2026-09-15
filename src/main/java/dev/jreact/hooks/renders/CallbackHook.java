package dev.jreact.hooks.renders;

import java.util.Arrays;

public class CallbackHook<T> {
    private T memoizedCallback;
    private Object[] lastDeps = null;

    public T bind(T callback, Object... deps) {
        if (memoizedCallback == null || !Arrays.equals(lastDeps, deps)) {
            memoizedCallback = callback;
            lastDeps = Arrays.copyOf(deps, deps.length);
        }
        return memoizedCallback;
    }

}
