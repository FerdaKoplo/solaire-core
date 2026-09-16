package dev.jreact.hooks.validations.libs;

import java.util.Date;

import dev.jreact.hooks.validations.SpoonValidator;

public class SpoonDate extends SpoonValidator<Date> {
    public SpoonDate required(String message) {
        rules.add(val -> (val == null) ? message : null);
        return this;
    }

    public SpoonDate before(Date maxDate, String message) {
        rules.add(val -> (val != null && val.after(maxDate)) ? message : null);
        return this;
    }

    public SpoonDate after(Date minDate, String message) {
        rules.add(val -> (val != null && val.before(minDate)) ? message : null);
        return this;
    }
}
