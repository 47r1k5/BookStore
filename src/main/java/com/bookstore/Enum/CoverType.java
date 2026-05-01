package com.bookstore.Enum;

import lombok.Getter;

@Getter
public enum CoverType {
    HARD("hard"),
    SOFT("soft"),
    E_BOOK("e-book");

    private final String dbValue;

    CoverType(String dbValue) {
        this.dbValue = dbValue;
    }

    public static CoverType fromDbValue(String value) {
        for (CoverType type : values()) {
            if (type.dbValue.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown cover_type value: " + value);
    }
}