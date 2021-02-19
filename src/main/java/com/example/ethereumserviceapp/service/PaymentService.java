package com.example.ethereumserviceapp.service;

import java.time.LocalDateTime;

public interface PaymentService {

    void startScheduledPayment();
    
    void startPayment(LocalDateTime dateNow, Boolean sync);
}
