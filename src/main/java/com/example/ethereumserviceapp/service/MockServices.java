package com.example.ethereumserviceapp.service;

import com.example.ethereumserviceapp.model.BooleanMockResult;
import com.example.ethereumserviceapp.model.UpdateMockResult;
import com.example.ethereumserviceapp.model.entities.SsiApplication;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MockServices {

    // Έλεγχος των ποσών που αφορούν σε επιδόματα ΟΑΕΔ

    /**
     * @param ssiApp             , the current applicaiton being checked if the value will update
     * @param shouldTry,boolean, denotes if the check should be made or not (if there is already a chance in this run
     *                           the check should be ignored)
     * @param ssiApps,           the household applications, related to the ssiApp parameter
     * @return 0 if no change should be made, else a change is made
     */
    public Optional<UpdateMockResult> getUpdatedOtherBenefitValue(LocalDate changeLowDate,LocalDate changeUpperDate,
                                                                  double pValue, SsiApplication ssiApp,
                                                                  boolean shouldTry,
                                                                  List<SsiApplication> ssiApps);

    public Optional<UpdateMockResult> getUpdatedERGOMValue(LocalDate changeLowDate,LocalDate changeUpperDate,double pValue,SsiApplication ssiApp, boolean shouldTry, List<SsiApplication> ssiApps);

    public Optional<UpdateMockResult> getUpdatedOAEDBenefitValue(LocalDate changeLowDate,LocalDate changeUpperDate, double pValue,SsiApplication ssiApp, boolean shouldTry, List<SsiApplication> ssiApps);

    public Optional<UpdateMockResult> getUpdateSalariesData(LocalDate changeLowDate,LocalDate changeUpperDate, double pValue,SsiApplication ssiApp, boolean shouldTry, List<SsiApplication> ssiApps);
    public Optional<UpdateMockResult> getUpdatedPension(LocalDate changeLowDate, LocalDate changeUpperDate, double pValue,SsiApplication ssiApp, boolean shouldTry, List<SsiApplication> ssiApps);
    public Optional<UpdateMockResult> getUpdatedFreelance(LocalDate changeLowDate, LocalDate changeUpperDate, double pValue,SsiApplication ssiApp, boolean shouldTry, List<SsiApplication> ssiApps);
    public Optional<UpdateMockResult> getUpdatedDepoists(LocalDate changeLowDate, LocalDate today,double pValue,SsiApplication ssiApp, boolean shouldTry, List<SsiApplication> ssiApps);

    public Optional<BooleanMockResult> getDeaths(LocalDate changeLowDate,LocalDate changeUpperDate,  double pValue,SsiApplication ssiApp, boolean shouldTry, List<SsiApplication> ssiApps);
    public Optional<BooleanMockResult> getOAEDRegistration(LocalDate changeLowDate, LocalDate changeUpperDate, double pValue,SsiApplication ssiApp, boolean shouldTry, List<SsiApplication> ssiApps);
    public Optional<BooleanMockResult> getLuxury(LocalDate changeLowDate, LocalDate changeUpperDate, double pValue,SsiApplication ssiApp, boolean shouldTry, List<SsiApplication> ssiApps);
}
