/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.ethereumserviceapp.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
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
import com.example.ethereumserviceapp.model.CasePayment;
import com.example.ethereumserviceapp.service.EthereumService;
import com.example.ethereumserviceapp.utils.ByteConverters;
import com.example.ethereumserviceapp.utils.ContractBuilder;
import com.example.ethereumserviceapp.utils.DateUtils;
import com.example.ethereumserviceapp.utils.RandomIdGenerator;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;
import org.web3j.tx.response.TransactionReceiptProcessor;
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
//        this.web3 = Web3j.build(new HttpService("https://ropsten.infura.io/v3/58249bdbdaf449d7b1cb4f3e1955ee77"));
        this.web3 = Web3j.build(new HttpService("http://I4mlab-besu.westeurope.cloudapp.azure.com:8545"));
        String password = null; // no encryption
        this.mnemonic = "talk prefer horse hope near copy time broken balance jaguar face scrap";
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
        this.CONTRACT_ADDRESS = System.getenv("CONTRACT_ADDRESS") == null ? "0xFa5B6432308d45B54A1CE1373513Fab77166436f" // old besu contract "0x345cA3e014Aaf5dcA488057592ee47305D9B3e10" // old ropsten contract 0x3027b1e481C3478E85f9adD58d239eD9742AB418
                : System.getenv("CONTRACT_ADDRESS");
        this.REVOCATION_CONTRACT_ADDRESS = System.getenv("REVOCATION_CONTRACT_ADDRESS") == null
                ? "0x9534d226e56826Cc4C01912Eb388b121Bb0683b5"
                : System.getenv("REVOCATION_CONTRACT_ADDRESS");
        txManager = new FastRawTransactionManager(web3, credentials);
    }

    @Override
    public Credentials getCredentials() {
        if (this.credentials == null) {
            this.web3 = Web3j.build(new HttpService("http://I4mlab-besu.westeurope.cloudapp.azure.com:8545"));//https://ropsten.infura.io/v3/58249bdbdaf449d7b1cb4f3e1955ee77
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
                Optional<Case> theCase = Optional.of(ContractBuilder.buildCaseFromTuple(this.getContract().getCase(byteUuid).send()));

                if(theCase.isPresent()){
                    ContractBuilder.linkPaymentToCase(this.getContract().getPayment(byteUuid).send(), theCase.get());
                }
                return theCase;
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
                    .addCase(uuid, BigInteger.valueOf(millis))
                    .encodeFunctionCall();
            
            this.txManager.sendTransaction(DefaultGasProvider.GAS_PRICE, BigInteger.valueOf(1000000),
                    contract.getContractAddress(), functionCall, BigInteger.ZERO).getTransactionHash();

        } catch (IOException ex) {
            log.info(ex.getMessage());
        }
    }

    @Override
    public void updateCase(Case monitoredCase) {
        //log.info("updateCase : synchronize transaction :{}", sync);
        if (this.checkIfCaseExists(monitoredCase.getUuid())) {
            try {

                log.info("updating case with uuid {} State {}", monitoredCase.getUuid(), monitoredCase.getState().getValue());
                LocalDateTime time = monitoredCase.getDate();

                ZonedDateTime zdt = time.atZone(ZoneId.of("America/Los_Angeles"));
                long millis = zdt.toInstant().toEpochMilli();
                long rjctMillis = monitoredCase.getRejectionDate().equals("")? 0L : DateUtils.historyDateStringToLDT(monitoredCase.getRejectionDate()).atZone(ZoneId.of("America/Los_Angeles")).toInstant().toEpochMilli();
                
                byte[] uuid = ByteConverters.stringToBytes16(monitoredCase.getUuid()).getValue();
                String functionCall = this.getContract()
                        .updateCase(uuid,
                                BigInteger.valueOf(millis), BigInteger.valueOf(monitoredCase.getState().getValue()),
                                 (monitoredCase.getDailyValue().multiply(BigDecimal.valueOf(100)).toBigInteger()),
                                 (monitoredCase.getDailySum().multiply(BigDecimal.valueOf(100)).toBigInteger()), 
                                 (monitoredCase.getOffset().multiply(BigDecimal.valueOf(100)).toBigInteger()),
                                  BigInteger.valueOf(rjctMillis) )
                        .encodeFunctionCall();
                String txHash = this.txManager.sendTransaction(DefaultGasProvider.GAS_PRICE, BigInteger.valueOf(1000000),
                        contract.getContractAddress(), functionCall, BigInteger.ZERO).getTransactionHash();

                // if(sync){
                //     TransactionReceiptProcessor receiptProcessor = new PollingTransactionReceiptProcessor(
                //         web3, 
                //         TransactionManager.DEFAULT_POLLING_FREQUENCY, 
                //         TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH);
                //     TransactionReceipt txReceipt = receiptProcessor.waitForTransactionReceipt(txHash);
                // }

            // } catch (TransactionException e){
            //     log.error(e.getMessage());
            } catch (IOException ex) {
                log.error(ex.getMessage());
            }
        } else {
            log.error("no case found for uuid {}", monitoredCase.getUuid());
        }
    }

    @Override
    public void addPayment(Case monitoredCase, CasePayment payment, Boolean sync){
        if (this.checkIfCaseExists(monitoredCase.getUuid())) {
            try {

                log.info("add new payment for case with uuid :{} and state :{}", monitoredCase.getUuid(), monitoredCase.getState().getValue());
                LocalDateTime time = monitoredCase.getDate();
                ZonedDateTime zdt = time.atZone(ZoneId.of("America/Los_Angeles"));
                long millis = zdt.toInstant().toEpochMilli();
                byte[] uuid = ByteConverters.stringToBytes16(monitoredCase.getUuid()).getValue();
                String functionCall = this.getContract()
                        .addPayment(uuid, BigInteger.valueOf(payment.getState().getValue()), 
                                BigInteger.valueOf(millis), payment.getPayment().multiply(BigDecimal.valueOf(100)).toBigInteger(), 
                                monitoredCase.getOffset().multiply(BigDecimal.valueOf(100)).toBigInteger())
                        .encodeFunctionCall();
                String txHash = this.txManager.sendTransaction(DefaultGasProvider.GAS_PRICE, BigInteger.valueOf(1000000),
                        contract.getContractAddress(), functionCall, BigInteger.ZERO).getTransactionHash();

                if(sync){
                    TransactionReceiptProcessor receiptProcessor = new PollingTransactionReceiptProcessor(
                        web3, 
                        TransactionManager.DEFAULT_POLLING_FREQUENCY, 
                        TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH);
                    TransactionReceipt txReceipt = receiptProcessor.waitForTransactionReceipt(txHash);
                }
            } catch (TransactionException e){
                log.error(e.getMessage());
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
