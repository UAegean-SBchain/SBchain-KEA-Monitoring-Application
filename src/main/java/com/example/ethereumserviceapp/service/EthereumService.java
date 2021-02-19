/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.ethereumserviceapp.service;

import java.util.List;
import java.util.Optional;

import com.example.ethereumserviceapp.contract.CaseMonitor;
import com.example.ethereumserviceapp.contract.VcRevocationRegistry;
import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.CasePayment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.web3j.crypto.Credentials;

/**
 *
 * @author nikos
 */
public interface EthereumService {

    Credentials getCredentials();

    CaseMonitor getContract();

    List<String> getAllCaseUUID();

    Optional<Case> getCaseByUUID(String uuid);

    void addCase(Case monitoredCase);

    void deleteCaseByUuid(String uuid);

    void updateCase(Case monitoredCase);

    //public void updateRejection(Case monitoredCase);

    void addPayment(Case monitoredCase, CasePayment payment, Boolean sync);

    boolean checkIfCaseExists(String uuid);

    VcRevocationRegistry getRevocationContract();

    boolean checkRevocationStatus(String uuid);

    Page<String> getCaseUuidsPaginated(Pageable pageable);

    //public void revokeCredentials(String uuid);
}
