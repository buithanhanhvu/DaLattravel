package com.example.dalattravel.model;

public enum RequestStatus {
    PENDING(0),
    MATCHED(1),
    CANCELLED(2),
    EXPIRED(3);

    private final int value;

    RequestStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
