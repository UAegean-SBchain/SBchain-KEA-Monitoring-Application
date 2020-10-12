package com.example.ethereumserviceapp.model;

public enum State {

    UNDEFINED(0, "Undefined"),
    ACCEPTED(1, "Accepted"), //case has been accepted
    REJECTED(2, "Rejected"), //case has been rejected
    PAID(3, "Paid"),
    PAUSED(4, "Paused");

    public final Integer value;
    public final String description;

    private State(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return this.value;
    }
}
