package dev.jreact.hooks.validations;

import dev.jreact.hooks.validations.libs.SpoonDate;
import dev.jreact.hooks.validations.libs.SpoonNumber;
import dev.jreact.hooks.validations.libs.SpoonString;

public class Spoon {
    private Spoon() {
    }

    public static SpoonString string() {
        return new SpoonString();
    }

    public static SpoonNumber number() {
        return new SpoonNumber();
    }

    public static SpoonDate date() {
        return new SpoonDate();
    }
}
