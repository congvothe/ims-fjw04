package com.fa.ims.enums;

public enum UserStatus {
    ACTIVATED("Activated"),
    DE_ACTIVATED("Deactivated");

    private final String label;

    UserStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
