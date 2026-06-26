package com.deliverytracker.domain;

public enum DeliveryStatus {
    PENDING("集荷待ち"),
    DELIVERED("配達完了");

    private final String label;

    DeliveryStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
