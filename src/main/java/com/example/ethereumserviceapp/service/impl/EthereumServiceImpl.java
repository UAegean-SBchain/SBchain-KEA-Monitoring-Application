/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.ethereumserviceapp.service.impl;

import com.example.ethereumserviceapp.contract.CaseMonitor;
import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.service.EthereumService;
import com.example.ethereumserviceapp.utils.ByteConverters;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Numeric;

/**
 *
 * @author nikos
 */
@Service
@Slf4j
public class EthereumServiceImpl implements EthereumService {

    private Web3j web3;
    private String mnemonic = "enlist addict era market spawn van medal despair melt shift sustain multiply";
    private Credentials credentials;
    private CaseMonitor contract;
    private final String CONTRACT_ADDRESS;

    public EthereumServiceImpl() {
        this.web3 = Web3j.build(new HttpService("https://ropsten.infura.io/v3/051806cbbf204a4886f2ab400c2c20f9"));
        String password = null; // no encryption
        this.mnemonic = "enlist addict era market spawn van medal despair melt shift sustain multiply";
        //Derivation path wanted: // m/44'/60'/0'/0 (this is used in ethereum, in bitcoin it is different
        int[] derivationPath = {44 | Bip32ECKeyPair.HARDENED_BIT, 60 | Bip32ECKeyPair.HARDENED_BIT, 0 | Bip32ECKeyPair.HARDENED_BIT, 0, 0};
        // Generate a BIP32 master keypair from the mnemonic phrase
        Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(MnemonicUtils.generateSeed(mnemonic, password));
        // Derived the key using the derivation path
        Bip32ECKeyPair derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, derivationPath);
        // Load the wallet for the derived key
        this.credentials = Credentials.create(derivedKeyPair);
        this.CONTRACT_ADDRESS = System.getenv("CONTRACT_ADDRESS") == null ? "0x3fF7e31E973E25071Db1E0c32B1e366f8aC5a265" : System.getenv("CONTRACT_ADDRESS");
    }

    @Override
    public Credentials getCredentials() {
        if (this.credentials == null) {
            this.web3 = Web3j.build(new HttpService("https://ropsten.infura.io/v3/051806cbbf204a4886f2ab400c2c20f9"));
            String password = null; // no encryption
            this.mnemonic = "enlist addict era market spawn van medal despair melt shift sustain multiply";
            //Derivation path wanted: // m/44'/60'/0'/0 (this is used in ethereum, in bitcoin it is different
            int[] derivationPath = {44 | Bip32ECKeyPair.HARDENED_BIT, 60 | Bip32ECKeyPair.HARDENED_BIT, 0 | Bip32ECKeyPair.HARDENED_BIT, 0, 0};
            // Generate a BIP32 master keypair from the mnemonic phrase
            Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(MnemonicUtils.generateSeed(mnemonic, password));
            // Derived the key using the derivation path
            Bip32ECKeyPair derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, derivationPath);
            // Load the wallet for the derived key
            this.credentials = Credentials.create(derivedKeyPair);
        }

        return this.credentials;
    }

    @Override
    public CaseMonitor getContract() {
        if (this.contract == null) {
            contract = CaseMonitor.load(this.CONTRACT_ADDRESS, this.web3, this.credentials, new DefaultGasProvider());
        }
        return this.contract;
    }

    @Override
    public List<String> getAllCaseUUID() {
        List<String> result = new ArrayList();
        try {
            List<byte[]> cases = this.getContract().getAllCases().sendAsync().get();
            cases.stream().forEach(caseId -> {
                result.add(ByteConverters.hexToASCII(Numeric.toHexStringNoPrefix((byte[]) caseId)));
            });
        } catch (InterruptedException ex) {
            log.error(ex.getMessage());
        } catch (ExecutionException ex) {
            log.error(ex.getMessage());
        }
        return result;
    }

    @Override
    public Case getCaseByUUID(String uuid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addCase(Case monitoredCase) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateCase(Case monitoredCase) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean checkIfCaseExists(String uuid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
