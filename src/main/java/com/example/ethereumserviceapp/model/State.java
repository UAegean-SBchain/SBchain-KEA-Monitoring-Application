package com.example.ethereumserviceapp.model;

public enum State {

    UNDEFINED(0, "Undefined"),  //case initial state
    ACCEPTED(1, "Accepted"),    //case has been accepted
    REJECTED(2, "Rejected"),    //case has been rejected
    PAID(3, "Paid"),            //case has been paid
    PAUSED(4, "Paused"),        //case has been paused/suspended
    FAILED(5, "Failed");        //payment of case has failed

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
