package com.fa.ims.enums;

public enum InterviewResult {
    OPEN("Open"),
    FAIL("Fail"),
    PASS("Pass"),
    CANCEL("Cancel");
    private final String label;

    InterviewResult(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}


