package com.fa.ims.enums;

public enum OfferStatus {
    WAITING_FOR_APPROVAL("Waiting for approval"),
    APPROVED_OFFER("Approved offer"),
    REJECTED_OFFER("Rejected offer"),
    CANCEL_OFFER("Cancel offer"),
    BANNED_OFFER("Banned");

    private final String label;

    OfferStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
