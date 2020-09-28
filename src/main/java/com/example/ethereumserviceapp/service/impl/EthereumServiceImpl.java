/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.ethereumserviceapp.service.impl;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import com.example.ethereumserviceapp.contract.CaseMonitor;
import com.example.ethereumserviceapp.contract.VcRevocationRegistry;
import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.service.EthereumService;
import com.example.ethereumserviceapp.utils.ByteConverters;
import com.example.ethereumserviceapp.utils.ContractBuilder;
import com.example.ethereumserviceapp.utils.RandomIdGenerator;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Numeric;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author nikos
 */
@Service
@Slf4j
public class EthereumServiceImpl implements EthereumService {

    private Web3j web3;
    private String mnemonic = "heavy peace decline bean recall budget trigger video era trash also unveil";
    private Credentials credentials;
    private CaseMonitor contract;
    private VcRevocationRegistry revocationContract;
    private final String CONTRACT_ADDRESS;
    private final String REVOCATION_CONTRACT_ADDRESS;
    private final TransactionManager txManager;

    public EthereumServiceImpl() {
        this.web3 = Web3j.build(new HttpService("https://ropsten.infura.io/v3/691797f6957f45e7944535265a9c13a6"));
        String password = null; // no encryption
        this.mnemonic = "heavy peace decline bean recall budget trigger video era trash also unveil";
        // Derivation path wanted: // m/44'/60'/0'/0 (this is used in ethereum, in
        // bitcoin it is different
        int[] derivationPath = {44 | Bip32ECKeyPair.HARDENED_BIT, 60 | Bip32ECKeyPair.HARDENED_BIT,
            0 | Bip32ECKeyPair.HARDENED_BIT, 0, 0};
        // Generate a BIP32 master keypair from the mnemonic phrase
        Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(MnemonicUtils.generateSeed(mnemonic, password));
        // Derived the key using the derivation path
        Bip32ECKeyPair derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, derivationPath);
        // Load the wallet for the derived key
        this.credentials = Credentials.create(derivedKeyPair);
        this.CONTRACT_ADDRESS = System.getenv("CONTRACT_ADDRESS") == null ? "0x3fF7e31E973E25071Db1E0c32B1e366f8aC5a265"
                : System.getenv("CONTRACT_ADDRESS");
        this.REVOCATION_CONTRACT_ADDRESS = System.getenv("REVOCATION_CONTRACT_ADDRESS") == null
                ? "0x9534d226e56826Cc4C01912Eb388b121Bb0683b5"
                : System.getenv("REVOCATION_CONTRACT_ADDRESS");
        txManager = new FastRawTransactionManager(web3, credentials);
    }

    @Override
    public Credentials getCredentials() {
        if (this.credentials == null) {
            this.web3 = Web3j.build(new HttpService("https://ropsten.infura.io/v3/691797f6957f45e7944535265a9c13a6"));
            String password = null; // no encryption
            this.mnemonic = "heavy peace decline bean recall budget trigger video era trash also unveil";
            // Derivation path wanted: // m/44'/60'/0'/0 (this is used in ethereum, in
            // bitcoin it is different
            int[] derivationPath = {44 | Bip32ECKeyPair.HARDENED_BIT, 60 | Bip32ECKeyPair.HARDENED_BIT,
                0 | Bip32ECKeyPair.HARDENED_BIT, 0, 0};
            // Generate a BIP32 master keypair from the mnemonic phrase
            Bip32ECKeyPair masterKeypair = Bip32ECKeyPair
                    .generateKeyPair(MnemonicUtils.generateSeed(mnemonic, password));
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
    public VcRevocationRegistry getRevocationContract() {
        if (this.revocationContract == null) {
            this.revocationContract = VcRevocationRegistry.load(this.REVOCATION_CONTRACT_ADDRESS, this.web3,
                    this.credentials, new DefaultGasProvider());
        }
        return this.revocationContract;
    }

    @Override
    public List<String> getAllCaseUUID() {
        List<String> result = new ArrayList<>();
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
    public Optional<Case> getCaseByUUID(String uuid) {
        List<String> cases = getAllCaseUUID();
        Optional<String> match = cases.stream().filter(caseId -> {
            // log.info("comparing |{}|{}|", caseId, uuid);
            return caseId.trim().equals(uuid.trim());
        }).findFirst();

        if (match.isPresent()) {
            byte[] byteUuid = ByteConverters.stringToBytes16(match.get()).getValue();
            try {
                return Optional.of(ContractBuilder.buildCaseFromTuple(this.getContract().getCase(byteUuid).send()));
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        }
        return Optional.empty();
    }

    @Override
    public void addCase(Case monitoredCase) {
        try {
            byte[] uuid;

            if (StringUtils.isEmpty(monitoredCase.getUuid())) {
                // UUIDs random cannot be encoded with only 16bytes (they are 32 min) so we use
                // Base 62 is used by tinyurl and bit.ly for the abbreviated URLs. It's a
                // well-understood method for creating "unique", human-readable IDs
                // But you need to check vs duplicates
                // https://stackoverflow.com/questions/9543715/generating-human-readable-usable-short-but-unique-ids
                String currentUUID = RandomIdGenerator.GetBase62(16);
                while (this.checkIfCaseExists(currentUUID)) {
                    currentUUID = RandomIdGenerator.GetBase62(16);
                }
                uuid = ByteConverters.stringToBytes16(currentUUID).getValue();
            } else {
                uuid = ByteConverters.stringToBytes16(monitoredCase.getUuid()).getValue();
            }
            LocalDateTime time = monitoredCase.getDate();
            if (time == null) {
                time = LocalDateTime.now();
            }
            ZonedDateTime zdt = time.atZone(ZoneId.of("America/Los_Angeles"));
            long millis = zdt.toInstant().toEpochMilli();
            String functionCall = this.getContract()
                    .addCase(uuid, monitoredCase.getName(), monitoredCase.getIsStudent(), BigInteger.valueOf(millis))
                    .encodeFunctionCall();
            this.txManager.sendTransaction(DefaultGasProvider.GAS_PRICE, BigInteger.valueOf(1000000),
                    contract.getContractAddress(), functionCall, BigInteger.ZERO).getTransactionHash();
        } catch (IOException ex) {
            log.info(ex.getMessage());
        }
    }

    @Override
    public void updateCase(Case monitoredCase) {
        if (this.checkIfCaseExists(monitoredCase.getUuid())) {
            try {

                log.info("updating case with uuid {} name {} isStudent {} State {}", monitoredCase.getUuid(),
                        monitoredCase.getName(), monitoredCase.getIsStudent(), monitoredCase.getState().getValue());
                LocalDateTime time = LocalDateTime.now();
                ZonedDateTime zdt = time.atZone(ZoneId.of("America/Los_Angeles"));
                long millis = zdt.toInstant().toEpochMilli();
                byte[] uuid = ByteConverters.stringToBytes16(monitoredCase.getUuid()).getValue();
                String functionCall = this.getContract()
                        .updateCase(uuid, monitoredCase.getName(), monitoredCase.getIsStudent(),
                                BigInteger.valueOf(millis), BigInteger.valueOf(monitoredCase.getState().getValue()))
                        .encodeFunctionCall();
                this.txManager.sendTransaction(DefaultGasProvider.GAS_PRICE, BigInteger.valueOf(1000000),
                        contract.getContractAddress(), functionCall, BigInteger.ZERO).getTransactionHash();
            } catch (IOException ex) {
                log.error(ex.getMessage());
            }
        } else {
            log.error("no case found for uuid {}", monitoredCase.getUuid());
        }

    }

    @Override
    public boolean checkIfCaseExists(String uuid) {
        List<String> existingIds = this.getAllCaseUUID();
        Optional<String> match = existingIds.stream().filter(caseId -> {
            return caseId.trim().equals(uuid.trim());
        }).findFirst();

        return match.isPresent();
    }

    public boolean checkRevocationStatus(String uuid) {
        try {
            byte[] theUuid = ByteConverters.stringToBytes32(uuid).getValue();
            Boolean result = this.getRevocationContract().isRevoked(theUuid).sendAsync().get();
            return result.booleanValue();
        } catch (InterruptedException ex) {
            log.error(ex.getMessage());
        } catch (ExecutionException ex) {
            log.error(ex.getMessage());
        }

        log.info("checking of teh revocation status failed for {}", uuid);
        return false;
    }

    // public void revokeCredentials(String uuid) {
    //     byte[] theUuid = ByteConverters.stringToBytes32(uuid).getValue();
    //     try {
    //         this.getRevocationContract().revoke(theUuid).sendAsync().get();
    //     } catch (InterruptedException ex) {
    //         log.error(ex.getMessage());
    //     } catch (ExecutionException ex) {
    //         log.error(ex.getMessage());
    //     }
    // }
    @Override
    public void deleteCaseByUuid(String uuid) {
        try {
            byte[] theUuid = ByteConverters.stringToBytes16(uuid).getValue();

            String functionCall = this.getContract()
                    .deleteCase(theUuid)
                    .encodeFunctionCall();
            this.txManager.sendTransaction(DefaultGasProvider.GAS_PRICE, BigInteger.valueOf(1000000),
                    contract.getContractAddress(), functionCall, BigInteger.ZERO).getTransactionHash();

        } catch (IOException ex) {
            log.error(ex.getMessage());
        }

    }
}
