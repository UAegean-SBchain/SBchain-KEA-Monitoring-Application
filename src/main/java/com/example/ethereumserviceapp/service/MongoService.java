/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.ethereumserviceapp.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.example.ethereumserviceapp.model.CredsAndExp;
import com.example.ethereumserviceapp.model.HouseholdMember;
import com.example.ethereumserviceapp.model.entities.SsiApplication;
import org.springframework.stereotype.Service;

/**
 *
 * @author nikos
 */
public interface MongoService {

    Optional<SsiApplication> findFirstByTaxisAfm(String taxisAfm);

    List<SsiApplication> findBySubmittedMunicipality(String municipality);

    List<SsiApplication> findByTaxisAfm(String taxisAfm);

    List<SsiApplication> findByTaxisAfmIn(Set<String> taxisAfms);

    Optional<SsiApplication> findByUuid(String uuid);

    List<SsiApplication> findAll();

    CredsAndExp[] findCredentialIdsByUuid(String uuid);

    List<SsiApplication> findByMeterNumber(String meterNumber);

    List<SsiApplication> findByIban(String iban);

    List<SsiApplication> findByHouseholdCompositionIn(Map<String, String> household);

    List<SsiApplication> findByHouseholdComposition(HouseholdMember member);

    List<SsiApplication> findByHouseholdPrincipalIn(List<HouseholdMember> members);

    List<String> findUuidByTaxisAfmIn(Set<String> taxisAfms);

    void deleteByUuid(String uuid);

    void updateSsiApp(SsiApplication ssiApp);
}
