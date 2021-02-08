package com.example.ethereumserviceapp.model;

public enum RejectionCode {
    
    REJECTION0(0, "NotRejected"), 
    REJECTION1(1, "Rejection1"),    
    REJECTION2(2, "Rejection2"),  
    REJECTION3(3, "Rejection3"), 
    REJECTION4(4, "Rejection4");

    public final Integer value;
    public final String description;

    private RejectionCode(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return this.value;
    }
}
