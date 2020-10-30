/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.ethereumserviceapp.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.ethereumserviceapp.model.ColletionOfCredentials;
import com.example.ethereumserviceapp.model.CredsAndExp;
import com.example.ethereumserviceapp.model.entities.SsiApplication;
import com.example.ethereumserviceapp.repository.SsiApplicationRepository;
import com.example.ethereumserviceapp.service.MongoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author nikos
 */
@Service
@Slf4j
public class MongoServiceImpl implements MongoService {

    private SsiApplicationRepository rep;

    @Autowired
    public MongoServiceImpl(SsiApplicationRepository rep) {
        this.rep = rep;
    }

    @Override
    public Optional<SsiApplication> findFirstByTaxisAfm(String taxisAfm) {
        return this.rep.findFirstByTaxisAfm(taxisAfm);
    }

    @Override
    public List<SsiApplication> findBySubmittedMunicipality(String municipality) {
        return this.rep.findBySubmittedMunicipality(municipality);
    }

    @Override
    public List<SsiApplication> findByTaxisAfm(String taxisAfm) {
        return this.rep.findByTaxisAfm(taxisAfm);
    }

    @Override
    public Optional<SsiApplication> findByUuid(String uuid) {
        return this.rep.findByUuid(uuid);
    }

    @Override
    public List<SsiApplication> findAll() {
        return this.rep.findAll();
    }

    @Override
    public CredsAndExp[] findCredentialIdsByUuid(String uuid) {
        String[] matches = this.rep.findCredentialIdsByUuid(uuid);
        if (matches.length == 1) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                ColletionOfCredentials creds = mapper.readValue(matches[0], ColletionOfCredentials.class);
                return creds.getCredentialIds();
            } catch (JsonProcessingException ex) {
                log.error(ex.getMessage());
            }
        } else {
            log.error("found {} credentials for case uuid {}", matches.length, uuid);
        }

        return new CredsAndExp[0];
    }
    
    @Override
    public List<SsiApplication> findByMeterNumber(String meterNumber) {
        return this.rep.findByMeterNumber(meterNumber);
    }

    @Override
    public List<SsiApplication> findByIban(String iban) {
        return this.rep.findByIban(iban);
    }

    @Override
    public List<SsiApplication> findByHouseholdCompositionIn(Map<String, String> household) {
        return this.rep.findByHouseholdCompositionIn(household);
    }

    @Override
    public void deleteByUuid(String uuid) {
        rep.deleteByUuid(uuid);
    }

}
