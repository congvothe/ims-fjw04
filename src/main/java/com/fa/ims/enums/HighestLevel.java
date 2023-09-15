package com.fa.ims.enums;

public enum HighestLevel {
    HIGH_SCHOOL("High School"),
    BACHELOR_S_DEGREE("Bachelor's Degree");

    private final String label;

    HighestLevel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
