package com.example.hhflow.camunda;

import org.camunda.bpm.client.task.ExternalTask;

public final class ExternalTaskVariables {

    private ExternalTaskVariables() {
    }

    public static Long getLong(ExternalTask task, String name) {
        Object value = requireVariable(task, name);
        if (value instanceof Long longValue) {
            return longValue;
        }
        if (value instanceof Integer intValue) {
            return intValue.longValue();
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        throw new IllegalArgumentException("Variable '" + name + "' is not numeric: " + value.getClass().getSimpleName());
    }

    public static String getString(ExternalTask task, String name) {
        Object value = requireVariable(task, name);
        if (value instanceof String stringValue) {
            return stringValue;
        }
        return String.valueOf(value);
    }

    public static Boolean getBoolean(ExternalTask task, String name) {
        Object value = requireVariable(task, name);
        if (value instanceof Boolean booleanValue) {
            return booleanValue;
        }
        throw new IllegalArgumentException("Variable '" + name + "' is not boolean: " + value.getClass().getSimpleName());
    }

    public static String getStringOrDefault(ExternalTask task, String name, String defaultValue) {
        Object value = task.getVariable(name);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof String stringValue) {
            return stringValue;
        }
        return String.valueOf(value);
    }

    private static Object requireVariable(ExternalTask task, String name) {
        Object value = task.getVariable(name);
        if (value == null) {
            throw new IllegalArgumentException("Required variable '" + name + "' is missing");
        }
        return value;
    }
}
