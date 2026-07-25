package com.example.DaLattravel.model;

public enum TripStatus {
    PENDING(0),
    IN_PROGRESS(1),
    COMPLETED(2),
    CANCELLED(3);

    private final int value;

    TripStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
