package dev.jreact.hooks.validations.libs;

import java.util.regex.Pattern;

import dev.jreact.hooks.validations.SpoonValidator;

public class SpoonString extends SpoonValidator<String> {
    private static final Pattern EMAIL_PATTERN = Pattern
            .compile("^[A-Za-z0-9+_.-]++@(?:[A-Za-z0-9-]++\\\\.)++[A-Za-z]{2,}$");
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]");
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("^[-+]?\\d++(?:\\.\\d++)?$");
    private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    private static final Pattern URL_PATTERN = Pattern.compile("^https?://(?:[a-zA-Z0-9-]++\\.)++[a-zA-Z]{2,}(?:/[^\\s]*+)?$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9\\s\\-]{7,20}$");

    // the core to check its validations
    public SpoonString required(String message) {
        rules.add(val -> (val == null || val.trim().isEmpty()) ? message : null);
        return this;
    }

    public SpoonString min(int length, String message) {
        rules.add(val -> (val != null && val.length() < length) ? message : null);
        return this;
    }

    public SpoonString max(int length, String message) {
        rules.add(val -> (val != null && val.length() > length) ? message : null);
        return this;
    }

    // checker
    public SpoonString email(String message) {
        rules.add(val -> (val != null && !val.trim().isEmpty() && !EMAIL_PATTERN.matcher(val).matches()) ? message
                : null);
        return this;
    }

    public SpoonString hasUppercase(String message) {
        rules.add(val -> (val != null && !UPPERCASE_PATTERN.matcher(val).matches()) ? message : null);
        return this;
    }

    public SpoonString hasLowercase(String message) {
        rules.add(val -> (val != null && !LOWERCASE_PATTERN.matcher(val).matches()) ? message : null);
        return this;
    }

    public SpoonString hasNumber(String message) {
        rules.add(val -> (val != null && !NUMBER_PATTERN.matcher(val).matches()) ? message : null);
        return this;
    }

    public SpoonString hasSpecialChar(String message) {
        rules.add(val -> (val != null && !SPECIAL_CHAR_PATTERN.matcher(val).matches()) ? message : null);
        return this;
    }

    public SpoonString isNumeric(String message) {
        rules.add(val -> (val != null && !val.trim().isEmpty() && !NUMERIC_PATTERN.matcher(val).matches()) ? message : null);
        return this;
    }

    public SpoonString uuid(String message) {
        rules.add(val -> (val != null && !val.trim().isEmpty() && !UUID_PATTERN.matcher(val).matches()) ? message : null);
        return this;
    }

    public SpoonString url(String message) {
        rules.add(val -> (val != null && !val.trim().isEmpty() && !URL_PATTERN.matcher(val).matches()) ? message : null);
        return this;
    }

    public SpoonString phone(String message) {
        rules.add(val -> (val != null && !val.trim().isEmpty() && !PHONE_PATTERN.matcher(val).matches()) ? message : null);
        return this;
    }

    public SpoonString in(String message, String... allowedValues) {
        java.util.List<String> allowed = java.util.Arrays.asList(allowedValues);
        rules.add(val -> (val != null && !allowed.contains(val)) ? message : null);
        return this;
    }

}
