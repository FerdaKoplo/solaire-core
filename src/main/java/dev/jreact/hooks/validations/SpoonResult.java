package dev.jreact.hooks.validations;

import java.util.List;

public record SpoonResult<T>(boolean success, T data, List<String> errors ) {
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
