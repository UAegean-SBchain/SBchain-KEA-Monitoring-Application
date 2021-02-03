package com.example.ethereumserviceapp.service;

import com.example.ethereumserviceapp.model.BooleanMockResult;
import com.example.ethereumserviceapp.model.UpdateMockResult;
import com.example.ethereumserviceapp.model.entities.SsiApplication;

import java.util.List;
import java.util.Optional;

public interface MockServices {

    // Έλεγχος των ποσών που αφορούν σε επιδόματα ΟΑΕΔ

    /**
     * @param ssiApp             , the current applicaiton being checked if the value will update
     * @param shouldTry,boolean, denotes if the check should be made or not (if there is already a chance in this run
     *                           the check should be ignored)
     * @param ssiApps,           all the applications submitted to the syste
     * @return 0 if no change should be made, else a change is made
     */
    public Optional<UpdateMockResult> getUpdatedOtherBenefitValue(SsiApplication ssiApp, boolean shouldTry, List<SsiApplication> ssiApps);

    public Optional<UpdateMockResult> getUpdatedERGOMValue(SsiApplication ssiApp, boolean shouldTry, List<SsiApplication> ssiApps);

    public Optional<UpdateMockResult> getUpdatedOAEDBenefitValue(SsiApplication ssiApp, boolean shouldTry, List<SsiApplication> ssiApps);

    public Optional<UpdateMockResult> getUpdateSalariesData(SsiApplication ssiApp, boolean shouldTry, List<SsiApplication> ssiApps);
    public Optional<UpdateMockResult> getUpdatedPension(SsiApplication ssiApp, boolean shouldTry, List<SsiApplication> ssiApps);
    public Optional<UpdateMockResult> getUpdatedFreelance(SsiApplication ssiApp, boolean shouldTry, List<SsiApplication> ssiApps);
    public Optional<UpdateMockResult> getUpdatedDepoists(SsiApplication ssiApp, boolean shouldTry, List<SsiApplication> ssiApps);

    public Optional<BooleanMockResult> getDeaths(SsiApplication ssiApp, boolean shouldTry, List<SsiApplication> ssiApps);
    public Optional<BooleanMockResult> getOAEDRegistration(SsiApplication ssiApp, boolean shouldTry, List<SsiApplication> ssiApps);
}
