package com.example.ethereumserviceapp.utils;

import org.apache.commons.math3.random.RandomDataGenerator;

public class SatisticUtils {

    private final static RandomDataGenerator rdg = new RandomDataGenerator();
//0.066666667
    public static int getSuccessesFromBinomial(double pValue) {
        return rdg.nextBinomial(30, pValue);
    }

    public static boolean shouldChangeValue(double pValue) {
        int sum = 0;
        for (int i = 0; i <= 30; i++) {
            int randomBinomialVariable
                    = getSuccessesFromBinomial(pValue);
            if (randomBinomialVariable == 0) sum++;
        }
//        System.out.println(sum);
        return sum == 0;
    }


}
