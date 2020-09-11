package com.example.ethereumserviceapp.utils;

public enum State {
    
    UNDEFINED (0, "Undefined"),
    ACCEPTED (1, "Accepted"),     //case has been accepted
    REJECTED (2, "Rejected"),     //case has been rejected
    PAID (3, "Paid");

    public final Integer value;
    public final String description;

    private State(Integer value, String description){
        this.value = value;
        this.description = description;
    }
}
