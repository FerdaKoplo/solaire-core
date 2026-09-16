package dev.jreact.hooks.validations;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class SpoonValidator<T> {
   protected final List<Function<T, String>> rules = new ArrayList<>();

    public SpoonResult<T> taste(T value) {
        List<String> errors = new ArrayList<>();
        for (Function<T, String> rule : rules) {
            String errorMsg = rule.apply(value);
            if (errorMsg != null) {
                errors.add(errorMsg);
            }
        }
        return new SpoonResult<>(errors.isEmpty(), value, errors);
    } 
}
