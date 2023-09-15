package com.fa.ims.enums;

public enum ContractType {
    TRIAL_2_MONTHS("Trial 2 months"),
    TRAINEE_3_MONTHS("Trainee 3 months"),
    TRAINEE_1_YEAR("Trainee 1 year"),
    TRAINEE_3_YEARS("Trainee 3 years"),
    TRAINEE_UNLIMITED("Trainee unlimited");

    private final String label;

    ContractType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
