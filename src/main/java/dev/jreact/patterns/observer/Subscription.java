package dev.jreact.patterns.observer;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Observer handle. unsubscribe() stops further notifications.
 */
@FunctionalInterface
public interface Subscription {
    void unsubscribe();
}
