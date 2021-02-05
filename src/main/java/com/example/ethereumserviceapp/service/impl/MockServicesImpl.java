package com.example.ethereumserviceapp.service.impl;


import com.example.ethereumserviceapp.model.BooleanMockResult;
import com.example.ethereumserviceapp.model.HouseholdMember;
import com.example.ethereumserviceapp.model.UpdateMockResult;
import com.example.ethereumserviceapp.model.entities.SsiApplication;
import com.example.ethereumserviceapp.service.MockServices;
import com.example.ethereumserviceapp.utils.EthAppUtils;
import com.example.ethereumserviceapp.utils.SatisticUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MockServicesImpl implements MockServices {


    private final Random random = new Random();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");




    @Override
    public Optional<UpdateMockResult> getUpdatedOtherBenefitValue(LocalDate dateOfSubmission, LocalDate today, double pValue,
                                                                  SsiApplication ssiApp, boolean shouldTry, List<SsiApplication> householdApps) {
        if (shouldTry) {
            if (SatisticUtils.shouldChangeValue(pValue)) {
                log.info("update value for otherBenefit !");
                double newOtherBenefits;
                // calculate threshold
                double otherBenefits = ssiApp.getOtherBenefitsR() != null ? Double.parseDouble(ssiApp.getOtherBenefitsR()) : 0;

                // all the applications of the household
                double threshold = threshold(ssiApp);
                SsiApplication aggregatedHouseholdValuesApp = EthAppUtils.aggregateHouseholdValues(householdApps);
                double aggregatedMonthlyIncome = getTotalIncome(aggregatedHouseholdValuesApp).doubleValue();
                // 50/50 chance for increase or decrease of the value
                if (random.nextBoolean()) {
                    // increase
                    log.info("INCREASING:: value");
                    int margin = (int) (threshold - aggregatedMonthlyIncome);
                    log.info("the margin is {}, increasing value by a max of this  + 500", margin);
                    //random.nextInt(max - min) + min;
                    newOtherBenefits = random.nextInt((margin+ 500) - (int)otherBenefits) + otherBenefits;
                } else {
                    log.info("DECREASING:: value");
                    int percentage = random.nextInt(100);
                    double amount = (otherBenefits * percentage) / 100;
                    newOtherBenefits = otherBenefits - amount;
                }

                int year = dateOfSubmission.getYear();
                int month = dateOfSubmission.getMonthValue();
                String updateMonth = String.format("%02d", random.nextInt(today.getMonthValue() - month + 1) + month - 1);

                String updateDay = String.format("%02d", random.nextInt(28 - 1) + 1);
                if (updateMonth.equals("00")) {
                    updateMonth = "12";
                    year -= 1;
                }
                LocalDate updateDate = LocalDate.parse(updateDay + "/" + updateMonth + "/" + year, formatter);
                UpdateMockResult result = new UpdateMockResult();
                result.setDate(updateDate);
                result.setValue(newOtherBenefits);
                result.setUuid(ssiApp.getUuid());
                log.info("threshod is {}, monthly aggregated income is {}, oriignal otherbenefit is {}", threshold, aggregatedMonthlyIncome, otherBenefits);
                log.info("updating value from {} to {}", otherBenefits, newOtherBenefits);
                return Optional.of(result);


            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<UpdateMockResult> getUpdatedERGOMValue(LocalDate dateOfSubmission,LocalDate today,
                                                           double pValue,SsiApplication ssiApp, boolean shouldTry,
                                                           List<SsiApplication> householdApps) {
        if (shouldTry) {
            if (SatisticUtils.shouldChangeValue(pValue)) {
                log.info("update value for otherBenefit !");
                double newErgom;
                // calculate threshold
                double ergom = ssiApp.getErgomeR() != null ? Double.parseDouble(ssiApp.getErgomeR()) : 0;
                double threshold = threshold(ssiApp);
                SsiApplication aggregatedHouseholdValuesApp = EthAppUtils.aggregateHouseholdValues(householdApps);
                double aggregatedMonthlyIncome = getTotalIncome(aggregatedHouseholdValuesApp).doubleValue();
                // 50/50 chance for increase or decrease of the value
                if (random.nextBoolean()) {
                    // increase
                    log.info("INCREASING:: value");
                    int margin = (int) (threshold - aggregatedMonthlyIncome);
                    log.info("the margin is {}, increasing value by a max of this  + 500", margin);
                    //random.nextInt(max - min) + min;
                    newErgom = random.nextInt((margin+ 500) - (int)ergom) + ergom;
                } else {
                    log.info("DECREASING:: value");
                    int percentage = random.nextInt(100);
                    double amount = (ergom * percentage) / 100;
                    newErgom = ergom - amount;
                }


                int year = dateOfSubmission.getYear();
                int month = dateOfSubmission.getMonthValue();
                String updateMonth = String.format("%02d", random.nextInt(today.getMonthValue() - month + 1) + month - 1);

                String updateDay = String.format("%02d", random.nextInt(28 - 1) + 1);
                if (updateMonth.equals("00")) {
                    updateMonth = "12";
                    year -= 1;
                }
                LocalDate updateDate = LocalDate.parse(updateDay + "/" + updateMonth + "/" + year, formatter);
                UpdateMockResult result = new UpdateMockResult();
                result.setDate(updateDate);
                result.setValue(newErgom);
                result.setUuid(ssiApp.getUuid());
                log.info("threshod is {}, monthly aggregated income is {}, original ergom is {}", threshold, aggregatedMonthlyIncome, ergom);
                log.info("updating value from {} to {}", ergom, newErgom);
                return Optional.of(result);


            }
        }
        return Optional.empty();
    }



    @Override
    public Optional<UpdateMockResult> getUpdatedOAEDBenefitValue(LocalDate dateOfSubmission,LocalDate today,
                                                                 double pValue,SsiApplication ssiApp, boolean shouldTry,
                                                                 List<SsiApplication> householdApps) {
        if (shouldTry) {
            if (SatisticUtils.shouldChangeValue(pValue)) {
                log.info("update value for otherBenefit !");
                double newOaedBenefit;
                // calculate threshold
                double oaedBenefit = ssiApp.getOtherBenefitsR() != null ? Double.parseDouble(ssiApp.getOtherBenefitsR()) : 0;
                double threshold = threshold(ssiApp);
                SsiApplication aggregatedHouseholdValuesApp = EthAppUtils.aggregateHouseholdValues(householdApps);
                double aggregatedMonthlyIncome = getTotalIncome(aggregatedHouseholdValuesApp).doubleValue();
                // 50/50 chance for increase or decrease of the value
                if (random.nextBoolean()) {
                    // increase
                    log.info("INCREASING:: value");
                    int margin = (int) (threshold - aggregatedMonthlyIncome);
                    log.info("the margin is {}, increasing value by a max of this  + 500", margin);
                    //random.nextInt(max - min) + min;
                    newOaedBenefit = random.nextInt((margin+ 500) - (int)oaedBenefit) + oaedBenefit;
                } else {
                    log.info("DECREASING:: value");
                    int percentage = random.nextInt(100);
                    double amount = (oaedBenefit * percentage) / 100;
                    newOaedBenefit = oaedBenefit - amount;
                }


                int year = dateOfSubmission.getYear();
                int month = dateOfSubmission.getMonthValue();
                String updateMonth = String.format("%02d", random.nextInt(today.getMonthValue() - month + 1) + month - 1);

                String updateDay = String.format("%02d", random.nextInt(28 - 1) + 1);
                if (updateMonth.equals("00")) {
                    updateMonth = "12";
                    year -= 1;
                }
                LocalDate updateDate = LocalDate.parse(updateDay + "/" + updateMonth + "/" + year, formatter);
                UpdateMockResult result = new UpdateMockResult();
                result.setDate(updateDate);
                result.setValue(newOaedBenefit);
                result.setUuid(ssiApp.getUuid());
                log.info("threshod is {}, monthly aggregated income is {}, original oaedBenefit is {}", threshold, aggregatedMonthlyIncome, oaedBenefit);
                log.info("updating value from {} to {}", oaedBenefit, newOaedBenefit);
                return Optional.of(result);


            }
        }
        return Optional.empty();
    }


    @Override
    public Optional<UpdateMockResult> getUpdateSalariesData(LocalDate dateOfSubmission,LocalDate today,
                                                            double pValue,SsiApplication ssiApp,
                                                            boolean shouldTry, List<SsiApplication> householdApps) {
        if (shouldTry) {
            if (SatisticUtils.shouldChangeValue(pValue)) {
                log.info("update value for otherBenefit !");
                double newSalaries = 0;
                // calculate threshold
                double salariesR = ssiApp.getSalariesR() != null ? Double.parseDouble(ssiApp.getSalariesR()) : 0;
                double threshold = threshold(ssiApp);
                SsiApplication aggregatedHouseholdValuesApp = EthAppUtils.aggregateHouseholdValues(householdApps);
                double aggregatedMonthlyIncome = getTotalIncome(aggregatedHouseholdValuesApp).doubleValue();
                // 50/50 chance for increase or decrease of the value
                if (random.nextBoolean()) {
                    // increase
                    log.info("INCREASING:: value");
                    int margin = (int) (threshold - aggregatedMonthlyIncome);
                    log.info("the margin is {}, increasing value by a max of this  + 500", margin);
                    //random.nextInt(max - min) + min;
                    newSalaries = random.nextInt((margin+ 500) - (int)salariesR) + salariesR;
                } else {
                    log.info("DECREASING:: value");
                    int percentage = random.nextInt(100 - 0) + 0;
                    double amount = (salariesR * percentage) / 100;
                    newSalaries = salariesR - amount;
                }


                int year = dateOfSubmission.getYear();
                int month = dateOfSubmission.getMonthValue();
                int day = dateOfSubmission.getDayOfMonth();
                String updateMonth = String.format("%02d", random.nextInt(today.getMonthValue() - month + 1) + month - 1);

                String updateDay = String.format("%02d", random.nextInt(28 - 1) + 1);
                if (updateMonth.equals("00")) {
                    updateMonth = "12";
                    year -= 1;
                }
                LocalDate updateDate = LocalDate.parse(updateDay + "/" + updateMonth + "/" + year, formatter);
                UpdateMockResult result = new UpdateMockResult();
                result.setDate(updateDate);
                result.setValue(newSalaries);
                result.setUuid(ssiApp.getUuid());
                log.info("threshod is {}, monthly aggregated income is {}, original Salaries is {}", threshold, aggregatedMonthlyIncome, salariesR);
                log.info("updating value from {} to {}", salariesR, newSalaries);
                return Optional.of(result);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<UpdateMockResult> getUpdatedPension(LocalDate dateOfSubmission,LocalDate today,
                                                        double pValue,SsiApplication ssiApp,
                                                        boolean shouldTry, List<SsiApplication> householdApps) {
        if (shouldTry) {
            if (SatisticUtils.shouldChangeValue(pValue)) {
                log.info("update value for otherBenefit !");
                double newPensions = 0;
                // calculate threshold
                double pensionsR = ssiApp.getPensionsR() != null ? Double.parseDouble(ssiApp.getPensionsR()) : 0;
                double threshold = threshold(ssiApp);
                SsiApplication aggregatedHouseholdValuesApp = EthAppUtils.aggregateHouseholdValues(householdApps);
                double aggregatedMonthlyIncome = getTotalIncome(aggregatedHouseholdValuesApp).doubleValue();
                // 50/50 chance for increase or decrease of the value
                if (random.nextBoolean()) {
                    // increase
                    log.info("INCREASING:: value");
                    int margin = (int) (threshold - aggregatedMonthlyIncome);
                    log.info("the margin is {}, increasing value by a max of this  + 500", margin);
                    //random.nextInt(max - min) + min;
                    newPensions = random.nextInt((margin+ 500) - (int)pensionsR) + pensionsR;
                } else {
                    log.info("DECREASING:: value");
                    int percentage = random.nextInt(100 - 0) + 0;
                    double amount = (pensionsR * percentage) / 100;
                    newPensions = pensionsR - amount;
                }


                int year = dateOfSubmission.getYear();
                int month = dateOfSubmission.getMonthValue();
                int day = dateOfSubmission.getDayOfMonth();
                String updateMonth = String.format("%02d", random.nextInt(today.getMonthValue() - month + 1) + month - 1);

                String updateDay = String.format("%02d", random.nextInt(28 - 1) + 1);
                if (updateMonth.equals("00")) {
                    updateMonth = "12";
                    year -= 1;
                }
                LocalDate updateDate = LocalDate.parse(updateDay + "/" + updateMonth + "/" + year, formatter);
                UpdateMockResult result = new UpdateMockResult();
                result.setDate(updateDate);
                result.setValue(newPensions);
                result.setUuid(ssiApp.getUuid());
                log.info("threshod is {}, monthly aggregated income is {}, original Pensions is {}", threshold, aggregatedMonthlyIncome, pensionsR);
                log.info("updating value from {} to {}", pensionsR, newPensions);
                return Optional.of(result);
            }
        }
        return Optional.empty();
    }


    @Override
    public Optional<UpdateMockResult> getUpdatedFreelance(LocalDate dateOfSubmission,LocalDate today,
                                                          double pValue,SsiApplication ssiApp, boolean shouldTry,
                                                          List<SsiApplication> householdApps) {
        if (shouldTry) {
            if (SatisticUtils.shouldChangeValue(pValue)) {
                log.info("update value for otherBenefit !");
                double newFreelanceR = 0;
                // calculate threshold
                double freelanceR = ssiApp.getFreelanceR() != null ? Double.parseDouble(ssiApp.getFreelanceR()) : 0;
                double threshold = threshold(ssiApp);
                SsiApplication aggregatedHouseholdValuesApp = EthAppUtils.aggregateHouseholdValues(householdApps);
                double aggregatedMonthlyIncome = getTotalIncome(aggregatedHouseholdValuesApp).doubleValue();
                // 50/50 chance for increase or decrease of the value
                if (random.nextBoolean()) {
                    // increase
                    log.info("INCREASING:: value");
                    int margin = (int) (threshold - aggregatedMonthlyIncome);
                    log.info("the margin is {}, increasing value by a max of this  + 500", margin);
                    //random.nextInt(max - min) + min;
                    newFreelanceR = random.nextInt((margin+ 500) - (int)freelanceR) + freelanceR;
                } else {
                    log.info("DECREASING:: value");
                    int percentage = random.nextInt(100 - 0) + 0;
                    double amount = (freelanceR * percentage) / 100;
                    newFreelanceR = freelanceR - amount;
                }


                int year = dateOfSubmission.getYear();
                int month = dateOfSubmission.getMonthValue();
                int day = dateOfSubmission.getDayOfMonth();
                String updateMonth = String.format("%02d", random.nextInt(today.getMonthValue() - month + 1) + month - 1);

                String updateDay = String.format("%02d", random.nextInt(28 - 1) + 1);
                if (updateMonth.equals("00")) {
                    updateMonth = "12";
                    year -= 1;
                }
                LocalDate updateDate = LocalDate.parse(updateDay + "/" + updateMonth + "/" + year, formatter);
                UpdateMockResult result = new UpdateMockResult();
                result.setDate(updateDate);
                result.setValue(newFreelanceR);
                result.setUuid(ssiApp.getUuid());
                log.info("threshod is {}, monthly aggregated income is {}, original Pensions is {}", threshold, aggregatedMonthlyIncome, freelanceR);
                log.info("updating value from {} to {}", freelanceR, newFreelanceR);
                return Optional.of(result);
            }
        }
        return Optional.empty();
    }


    @Override
    public Optional<UpdateMockResult> getUpdatedDepoists(LocalDate dateOfSubmission,LocalDate today,
                                                         double pValue,SsiApplication ssiApp, boolean shouldTry,
                                                         List<SsiApplication> householdApps) {
        //financial data :
        if (shouldTry) {
            if (SatisticUtils.shouldChangeValue(pValue)) {
                log.info("update value for otherBenefit !");
                double newDeposits = 0;
                // calculate threshold
                double depositsR = ssiApp.getDepositsA() != null ? Double.parseDouble(ssiApp.getDepositsA()) : 0;
                double threshold = threshold(ssiApp);
                SsiApplication aggregatedHouseholdValuesApp = EthAppUtils.aggregateHouseholdValues(householdApps);
                double aggregatedMonthlyIncome = getTotalIncome(aggregatedHouseholdValuesApp).doubleValue();
                // 50/50 chance for increase or decrease of the value
                if (random.nextBoolean()) {
                    // increase
                    log.info("INCREASING:: value");
                    int margin = (int) (threshold - aggregatedMonthlyIncome);
                    log.info("the margin is {}, increasing value by a max of this  + 500", margin);
                    //random.nextInt(max - min) + min;
                    newDeposits = random.nextInt((margin+ 500) - (int)depositsR) + depositsR;
                } else {
                    log.info("DECREASING:: value");
                    int percentage = random.nextInt(100 - 0) + 0;
                    double amount = (depositsR * percentage) / 100;
                    newDeposits = depositsR - amount;
                }


                int year = dateOfSubmission.getYear();
                int month = dateOfSubmission.getMonthValue();
                String updateMonth = String.format("%02d", random.nextInt(today.getMonthValue() - month + 1) + month - 1);

                String updateDay = String.format("%02d", random.nextInt(28 - 1) + 1);
                if (updateMonth.equals("00")) {
                    updateMonth = "12";
                    year -= 1;
                }
                LocalDate updateDate = LocalDate.parse(updateDay + "/" + updateMonth + "/" + year, formatter);
                UpdateMockResult result = new UpdateMockResult();
                result.setDate(updateDate);
                result.setValue(newDeposits);
                result.setUuid(ssiApp.getUuid());
                log.info("threshod is {}, monthly aggregated income is {}, original Pensions is {}", threshold, aggregatedMonthlyIncome, depositsR);
                log.info("updating value from {} to {}", depositsR, newDeposits);
                return Optional.of(result);
            }
        }
        return Optional.empty();
    }


    @Override
    public Optional<BooleanMockResult> getDeaths(LocalDate dateOfSubmission,LocalDate today,double pValue,SsiApplication ssiApp,
                                                 boolean shouldTry, List<SsiApplication> householdApps) {
        if (shouldTry) {
            if (SatisticUtils.shouldChangeValue(pValue)) {
                Optional<HouseholdMember> deceased = ssiApp.getHouseholdComposition().stream().
                        filter( person -> !person.getAfm().equals(ssiApp.getHouseholdPrincipal().getAfm())).findFirst();


                int year = dateOfSubmission.getYear();
                int month = dateOfSubmission.getMonthValue();
                String updateMonth = String.format("%02d", random.nextInt(today.getMonthValue() - month + 1) + month - 1);

                String updateDay = String.format("%02d", random.nextInt(28 - 1) + 1);
                if (updateMonth.equals("00")) {
                    updateMonth = "12";
                    year -= 1;
                }
                LocalDate updateDate = LocalDate.parse(updateDay + "/" + updateMonth + "/" + year, formatter);
                BooleanMockResult result = new BooleanMockResult();
                result.setDate(updateDate);
                result.setValue(true);
                result.setData(deceased.toString());
                result.setUuid(ssiApp.getUuid());
                return Optional.of(result);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<BooleanMockResult> getOAEDRegistration(LocalDate dateOfSubmission,LocalDate today,double pValue,
                                                           SsiApplication ssiApp, boolean shouldTry, List<SsiApplication> householdApps) {
        if (shouldTry) {
            if (SatisticUtils.shouldChangeValue(pValue)) {
                final LocalDate referenceDate = LocalDate.now();
                Optional<HouseholdMember> adultMember = ssiApp.getHouseholdComposition().stream().filter( person -> !person.getAfm().equals(ssiApp.getHouseholdPrincipal().getAfm()) &&
                        (EthAppUtils.calculateAge(LocalDate.parse(person.getDateOfBirth(), formatter), referenceDate) >= 18)).findFirst();


                int year = dateOfSubmission.getYear();
                int month = dateOfSubmission.getMonthValue();
                String updateMonth = String.format("%02d", random.nextInt(today.getMonthValue() - month + 1) + month - 1);

                String updateDay = String.format("%02d", random.nextInt(28 - 1) + 1);
                if (updateMonth.equals("00")) {
                    updateMonth = "12";
                    year -= 1;
                }
                LocalDate updateDate = LocalDate.parse(updateDay + "/" + updateMonth + "/" + year, formatter);
                BooleanMockResult result = new BooleanMockResult();
                result.setDate(updateDate);
                result.setValue(true);
                result.setData(adultMember.toString());
                result.setUuid(ssiApp.getUuid());
                return Optional.of(result);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<BooleanMockResult> getLuxury(LocalDate dateOfSubmission,LocalDate today, double pValue,
                                                 SsiApplication ssiApp, boolean shouldTry, List<SsiApplication> householdApps) {
        if (shouldTry) {
            if (SatisticUtils.shouldChangeValue(pValue)) {
                int year = dateOfSubmission.getYear();
                int month = dateOfSubmission.getMonthValue();
                String updateMonth = String.format("%02d", random.nextInt(today.getMonthValue() - month + 1) + month - 1);

                String updateDay = String.format("%02d", random.nextInt(28 - 1) + 1);
                if (updateMonth.equals("00")) {
                    updateMonth = "12";
                    year -= 1;
                }
                LocalDate updateDate = LocalDate.parse(updateDay + "/" + updateMonth + "/" + year, formatter);
                BooleanMockResult result = new BooleanMockResult();
                result.setDate(updateDate);
                result.setValue(true);
                result.setData(null);
                result.setUuid(ssiApp.getUuid());
                return Optional.of(result);
            }
        }
        return Optional.empty();
    }









    private static double threshold(SsiApplication ssiApp) {
        List<HouseholdMember> household = ssiApp.getHouseholdComposition();
        final LocalDate referenceDate = LocalDate.now();
        long adults = household.stream().filter(h -> EthAppUtils.calculateAge(LocalDate.parse(h.getDateOfBirth(), formatter), referenceDate) >= 18).count();
        int adultCount = (int) adults;
        int minorCount = household.size() - adultCount;
        // remove one adult because the first one has a fixed payment value of 200
        if (adultCount == 0 && minorCount > 0) {
            adultCount = minorCount - 1;
        } else if (adultCount == 1 && ssiApp.getParenthood() != null && ssiApp.getParenthood().equals("single") && minorCount > 0) {
            minorCount--;
        } else if ((adultCount == 1 && minorCount == 0) || adultCount >= 2) {
            adultCount--;
        } else if (adultCount == 1 && ssiApp.getParenthood() != null && !ssiApp.getParenthood().equals("single")) {
            adultCount = adultCount + minorCount - 1;
        }
//        log.info("adult count :{}, minor count :{}", adultCount, minorCount);
        return BigDecimal.valueOf(6).multiply(BigDecimal.valueOf(200)
                .add((BigDecimal.valueOf(adultCount).multiply(BigDecimal.valueOf(100))
                        .add(BigDecimal.valueOf(minorCount).multiply(BigDecimal.valueOf(50)))))).doubleValue();
    }


    private BigDecimal getTotalIncome(SsiApplication ssiApp) {
        BigDecimal salaries = new BigDecimal(ssiApp.getSalariesR() == null ? "0" : ssiApp.getSalariesR()).subtract(new BigDecimal(ssiApp.getSalariesR() == null ? "0" : ssiApp.getSalariesR()).multiply(BigDecimal.valueOf(0.2)));
        BigDecimal pensions = new BigDecimal(ssiApp.getPensionsR() == null ? "0" : ssiApp.getPensionsR());
        BigDecimal farming = new BigDecimal(ssiApp.getFarmingR() == null ? "0" : ssiApp.getFarmingR());
        BigDecimal freelance = new BigDecimal(ssiApp.getFreelanceR() == null ? "0" : ssiApp.getFreelanceR());
        BigDecimal otherBnfts = new BigDecimal(ssiApp.getOtherBenefitsR() == null ? "0" : ssiApp.getOtherBenefitsR());
        BigDecimal deposits = new BigDecimal(ssiApp.getDepositsA() == null ? "0" : ssiApp.getDepositsA());
        BigDecimal domesticRe = new BigDecimal(ssiApp.getDomesticRealEstateA() == null ? "0" : ssiApp.getDomesticRealEstateA());
        BigDecimal foreignRe = new BigDecimal(ssiApp.getForeignRealEstateA() == null ? "0" : ssiApp.getForeignRealEstateA());

        return (salaries.add(
                pensions).add(
                farming).add(
                freelance).add(
                otherBnfts).add(
                deposits).add(
                domesticRe).add(
                foreignRe)
        ).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

    }


}
