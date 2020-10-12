package com.example.ethereumserviceapp.utils;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.State;
import com.example.ethereumserviceapp.model.entities.SsiApplication;
import com.example.ethereumserviceapp.repository.SsiApplicationRepository;

import org.springframework.beans.factory.annotation.Autowired;

public class PaymentUtils {

    @Autowired
    private SsiApplicationRepository ssiRepo;
    
    public Boolean checkPaymentCredentials(Case caseToBePaid, SsiApplication ssiApp){
        //mock household check
        Map<String, String>[] houseHold = ssiApp.getHouseholdComposition();
        for(int i = 0; i < houseHold.length; i++){
            if(houseHold[i].entrySet().stream().anyMatch(h -> h.getValue().equals("deceased"))){
                return false;
            }
        }
        //external oaed check
        if(!oaedRegistrationCheck(ssiApp.getOaedId())){
            return false;
        }
        //external housing subsidy check
        if(houseBenefitCheck(ssiApp.getTaxisAfm())){
            return false;
        }
        // check if meter number appears on other applications
        if(ssiRepo.findByMeterNumber(ssiApp.getMeterNumber()).size() > 1){
            return false;
        }
        // check for luxury living
        if(ssiApp.getLuxury().equals(String.valueOf(Boolean.TRUE))){
            return false;
        }
        //check OAED benefits
        if(BigInteger.valueOf(Long.valueOf(ssiApp.getUnemploymentBenefitR())).compareTo(BigInteger.valueOf(500)) > 0 ){
            return false;
        }
        //check other benefits
        if(BigInteger.valueOf(Long.valueOf(ssiApp.getOtherBenefitsR())).compareTo(BigInteger.valueOf(500)) > 0 ){
            return false;
        }
        //check Ergome benefits
        if(BigInteger.valueOf(Long.valueOf(ssiApp.getErgomeR())).compareTo(BigInteger.valueOf(500)) > 0 ){
            return false;
        }
        //check if two months have passed while the application is in status paused
        Iterator<Entry<LocalDateTime, State>> it = caseToBePaid.getHistory().entrySet().iterator();
        int count = 0;
        while(it.hasNext()){
            if(count > 59){
                break;
            }
            Map.Entry<LocalDateTime, State> entry = caseToBePaid.getHistory().entrySet().iterator().next();
            if(entry.getValue().equals(State.PAUSED) && (it.hasNext() && it.next().getValue().equals(State.PAUSED) || count == 59)){
                count++;
                
            } else {
                count = 0;
            }
        }

        if(count > 59){
            return false;
        }
        // check that if there differences in Amka register
        if(differenceInAmka(ssiApp.getTaxisAmka())){
            return false;
        }
        //check if iban exists in other application
        if(ssiRepo.findByIban(ssiApp.getIban()).size() > 1){
            return false;
        }

        return true;
    }

    //mock oaed registration check
    private Boolean oaedRegistrationCheck(String oaedId){
        return true;
    }
    //mock housing subsidy check
    private Boolean houseBenefitCheck(String afm){
        return true;
    }

    //mockAmkaCheck
    private Boolean differenceInAmka(String amka){
        return false;
    }
}
