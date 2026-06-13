package com.example.hhflow.validation;

public final class ValidationConstraints {

    public static final int EMAIL_MAX_LENGTH = 254;
    public static final int PHONE_MAX_LENGTH = 32;
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 128;
    public static final int PASSWORD_HASH_MAX_LENGTH = 128;
    public static final int TITLE_MAX_LENGTH = 150;
    public static final int FULL_NAME_MAX_LENGTH = 150;
    public static final int SUMMARY_MAX_LENGTH = 2000;
    public static final int ROLE_MAX_LENGTH = 32;

    public static final int PAGE_DEFAULT = 0;
    public static final int PAGE_MIN = 0;
    public static final String PAGE_DEFAULT_VALUE = "0";

    public static final int SIZE_DEFAULT = 20;
    public static final int SIZE_MIN = 1;
    public static final int SIZE_MAX = 100;
    public static final String SIZE_DEFAULT_VALUE = "20";

    public static final int ID_MIN = 1;

    private ValidationConstraints() {
    }
}