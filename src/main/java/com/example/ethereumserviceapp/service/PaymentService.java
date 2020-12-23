package com.example.ethereumserviceapp.service;

import java.time.LocalDateTime;

public interface PaymentService {

    public void startScheduledPayment();
    
    public void startPayment(LocalDateTime dateNow, String uuid, Boolean sync);
}
