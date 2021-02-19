package com.example.ethereumserviceapp.model;

public enum RejectionCode {
    
    REJECTION0(0, "NotRejected"), 
    REJECTION101(1, "Rejection101"),    
    REJECTION102(2, "Rejection102"),  
    REJECTION103(3, "Rejection103"), 
    REJECTION104(4, "Rejection104"),
    REJECTION105(5, "Rejection105"),  
    REJECTION107(7, "Rejection107"), 
    REJECTION108(8, "Rejection108"),
    REJECTION109(9, "Rejection109"),  
    REJECTION110(10, "Rejection110");

    public final Integer value;
    public final String description;

    RejectionCode(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return this.value;
    }
}
