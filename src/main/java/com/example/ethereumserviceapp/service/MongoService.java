/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.ethereumserviceapp.service;

import com.example.ethereumserviceapp.model.CredsAndExp;
import com.example.ethereumserviceapp.model.entities.SsiApplication;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author nikos
 */
public interface MongoService {

    public Optional<SsiApplication> findFirstByTaxisAfm(String taxisAfm);

    public List<SsiApplication> findBySubmittedMunicipality(String municipality);

    public List<SsiApplication> findByTaxisAfm(String taxisAfm);

    public Optional<SsiApplication> findByUuid(String uuid);

    public List<SsiApplication> findAll();

    public CredsAndExp[] findCredentialIdsByUuid(String uuid);

    public void deleteByUuid(String uuid);
}
