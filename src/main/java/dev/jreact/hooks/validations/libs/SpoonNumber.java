package dev.jreact.hooks.validations.libs;

import dev.jreact.hooks.validations.SpoonValidator;

public class SpoonNumber extends SpoonValidator<Double> {
    public SpoonNumber required(String message) {
        rules.add(val -> (val == null) ? message : null);
        return this;
    }

    public SpoonNumber min(double min, String message) {
        rules.add(val -> (val != null && val < min) ? message : null);
        return this;
    }

    public SpoonNumber max(double max, String message) {
        rules.add(val -> (val != null && val > max) ? message : null);
        return this;
    }

    public SpoonNumber positive(String message) {
        rules.add(val -> (val != null && val <= 0) ? message : null);
        return this;
    }

    public SpoonNumber negative(String message) {
        rules.add(val -> (val != null && val >= 0) ? message : null);
        return this;
    }

    public SpoonNumber integer(String message) {
        rules.add(val -> (val != null && val % 1 != 0) ? message : null);
        return this;
    }
    
}
