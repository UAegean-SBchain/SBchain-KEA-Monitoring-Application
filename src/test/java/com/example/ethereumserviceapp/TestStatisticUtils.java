package com.example.ethereumserviceapp;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Test;

public class TestStatisticUtils {


    @Test
    public void testGetBiominal(){
        RandomDataGenerator rdg = new RandomDataGenerator();
        int sum = 0;
        for(int i = 0; i <= 30; i++) {
//                System.out.println(getBinomial(30,0.066666667));
            int randomBinomialVariable
                    = rdg.nextBinomial(30, 0.066666667);
            System.out.println(randomBinomialVariable); //number of succseful experiments after 30 trials
            if(randomBinomialVariable == 0) sum++;
        }
        System.out.println("THE SUM IS " + sum);
    }
}
