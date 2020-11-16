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

/**
 *
 * @author nikos
 */
public interface MongoService {

    public Optional<SsiApplication> findFirstByTaxisAfm(String taxisAfm);

    public List<SsiApplication> findBySubmittedMunicipality(String municipality);

    public List<SsiApplication> findByTaxisAfm(String taxisAfm);

    public List<SsiApplication> findByTaxisAfmIn(Set<String> taxisAfms);

    public Optional<SsiApplication> findByUuid(String uuid);

    public List<SsiApplication> findAll();

    public CredsAndExp[] findCredentialIdsByUuid(String uuid);

    public List<SsiApplication> findByMeterNumber(String meterNumber);

    public List<SsiApplication> findByIban(String iban);

    public List<SsiApplication> findByHouseholdCompositionIn(Map<String, String> household);

    public List<SsiApplication> findByHouseholdComposition(HouseholdMember member);

    public List<SsiApplication> findByHouseholdPrincipalIn(List<HouseholdMember> members);

    public void deleteByUuid(String uuid);
}
