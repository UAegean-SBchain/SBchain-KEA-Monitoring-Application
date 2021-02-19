/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.ethereumserviceapp.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import org.apache.commons.codec.binary.Hex;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author nikos
 */
@Service
@Slf4j
public class EthereumServiceImpl implements EthereumService {

    private Web3j web3;
    private String mnemonic = "";
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
        this.CONTRACT_ADDRESS = System.getenv("CONTRACT_ADDRESS") == null ? "0xDa04fa66Bd544fAc14214Da9862F41447Ee55c71"
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
            this.mnemonic = "talk prefer horse hope near copy time broken balance jaguar face scrap";
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
                result.add(ByteConverters.hexToASCII(Numeric.toHexStringNoPrefix(caseId)));
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
        // List<String> cases = getAllCaseUUID();
        // Optional<String> match = cases.stream().filter(caseId -> {
        //     log.info("comparing |{}|{}|", caseId, uuid);
        //     return caseId.trim().equals(uuid.trim());
        // }).findFirst();

        // if (match.isPresent()) {
            //byte[] byteUuid = ByteConverters.stringToBytes16(match.get()).getValue();
            byte[] byteUuid = ByteConverters.stringToBytes16(uuid).getValue();
            try {
                Optional<Case> theCase = Optional.of(ContractBuilder.buildCaseFromTuple(this.getContract().getCase(byteUuid).send()));
                if(theCase.isPresent()){
                    ContractBuilder.linkPaymentToCase(this.getContract().getPayment(byteUuid).send(), theCase.get());
                    ContractBuilder.linkRejectionToCase(this.getContract().getRejection(byteUuid).send(), theCase.get());
                    return theCase;
                }
                return Optional.empty();
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
       // }
        return Optional.empty();
    }

    @Override
    public void addCase(Case monitoredCase) {
        log.info("addCase Called");
        try {
            byte[] uuid;
            log.info("trying to add {}", monitoredCase.getUuid());
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
                log.info("!!!the uuid HEX is: "+ Hex.encodeHexString( uuid ));
            }
            LocalDateTime time = monitoredCase.getDate();
            if (time == null) {
                time = LocalDateTime.now();
            }
            ZonedDateTime zdt = time.atZone(ZoneId.of("Europe/Athens"));
            long millis = zdt.toInstant().toEpochMilli();
            String functionCall = this.getContract()
                    .addCase(uuid, BigInteger.valueOf(millis))
                    .encodeFunctionCall();
            //,
            String hash = this.txManager.sendTransaction(DefaultGasProvider.GAS_PRICE , BigInteger.valueOf(1000000),
                    contract.getContractAddress(), functionCall, BigInteger.ZERO).getTransactionHash();

            log.info("transaciton sent with hash:{}", hash);
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

                ZonedDateTime zdt = time.atZone(ZoneId.of("Europe/Athens"));
                long millis = zdt.toInstant().toEpochMilli();
                long rjctMillis = monitoredCase.getRejectionDate() == null || monitoredCase.getRejectionDate().equals("")? 0L : DateUtils.dateStringToLD(monitoredCase.getRejectionDate()).atStartOfDay(ZoneId.of("Europe/Athens")).toInstant().toEpochMilli();
                byte[] uuid = ByteConverters.stringToBytes16(monitoredCase.getUuid()).getValue();
                String functionCall = this.getContract()
                        .updateCase(uuid,
                                BigInteger.valueOf(millis), BigInteger.valueOf(monitoredCase.getState().getValue()),
                                 (monitoredCase.getDailyValue() != null? monitoredCase.getDailyValue().multiply(BigDecimal.valueOf(100)).toBigInteger() : BigInteger.valueOf(0)),
                                 (monitoredCase.getDailySum() != null? monitoredCase.getDailySum().multiply(BigDecimal.valueOf(100)).toBigInteger() : BigInteger.valueOf(0)), 
                                 (monitoredCase.getOffset() != null? monitoredCase.getOffset().multiply(BigDecimal.valueOf(100)).toBigInteger() : BigInteger.valueOf(0)),
                                 BigInteger.valueOf(monitoredCase.getRejectionCode().getValue()), 
                                 BigInteger.valueOf(rjctMillis))
                        .encodeFunctionCall();
                this.txManager.sendTransaction(DefaultGasProvider.GAS_PRICE, BigInteger.valueOf(1000000),
                        contract.getContractAddress(), functionCall, BigInteger.ZERO).getTransactionHash();

                

            //     if(monitoredCase.getState().equals(State.REJECTED)){
            //         TransactionReceiptProcessor receiptProcessor = new PollingTransactionReceiptProcessor(
            //             web3, 
            //             TransactionManager.DEFAULT_POLLING_FREQUENCY, 
            //             TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH);
            //         TransactionReceipt txReceipt = receiptProcessor.waitForTransactionReceipt(txHash);
            //     }

            // } catch (TransactionException e){
            //     log.error(e.getMessage());
            } catch (IOException ex) {
                log.error(ex.getMessage());
            }
        } else {
            log.error("no case found for uuid {}", monitoredCase.getUuid());
        }
    }

    // @Override
    // public void updateRejection(Case monitoredCase){

    //     if (this.checkIfCaseExists(monitoredCase.getUuid())) {
    //         try {

    //             log.info("reject case :{} with date :{} and code :{}", monitoredCase.getUuid(), monitoredCase.getRejectionDate(), monitoredCase.getRejectionCode());

    //             long rjctMillis = monitoredCase.getRejectionDate() == null || monitoredCase.getRejectionDate().equals("")? 0L : DateUtils.dateStringToLD(monitoredCase.getRejectionDate()).atStartOfDay(ZoneId.of("Europe/Athens")).toInstant().toEpochMilli();

    //             log.info("reject date millis :{}, big int Code :{}, bigIntDate :{}", rjctMillis, BigInteger.valueOf(monitoredCase.getRejectionCode().getValue()), BigInteger.valueOf(rjctMillis));
                
    //             byte[] uuid = ByteConverters.stringToBytes16(monitoredCase.getUuid()).getValue();
    //             String functionCall = this.getContract()
    //                     .updateRejection(uuid, BigInteger.valueOf(monitoredCase.getRejectionCode().getValue()), 
    //                         BigInteger.valueOf(rjctMillis))
    //                     .encodeFunctionCall();
    //             String txHash = this.txManager.sendTransaction(DefaultGasProvider.GAS_PRICE, BigInteger.valueOf(1000000),
    //                     contract.getContractAddress(), functionCall, BigInteger.ZERO).getTransactionHash();

    //             // if(sync){
    //             //     TransactionReceiptProcessor receiptProcessor = new PollingTransactionReceiptProcessor(
    //             //         web3, 
    //             //         TransactionManager.DEFAULT_POLLING_FREQUENCY, 
    //             //         TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH);
    //             //     TransactionReceipt txReceipt = receiptProcessor.waitForTransactionReceipt(txHash);
    //             // }
    //         } catch (IOException ex) {
    //             log.error(ex.getMessage());
    //         }
    //     } else {
    //         log.error("no case found for uuid {}", monitoredCase.getUuid());
    //     }
    //     // if there is a rejection then update rejection struct
    //     // if(!monitoredCase.getRejectionCode().equals(RejectionCode.REJECTION0)){
    //     //     long rjctMillis = monitoredCase.getRejectionDate().equals("") || monitoredCase.getRejectionDate() ==null? 0L : DateUtils.dateStringToLD(monitoredCase.getRejectionDate()).atStartOfDay(ZoneId.of("Europe/Athens")).toInstant().toEpochMilli();

    //     //     String rejectionCall = this.getContract().updateRejection(uuid, BigInteger.valueOf(monitoredCase.getRejectionCode().getValue())
    //     //     , BigInteger.valueOf(rjctMillis)).encodeFunctionCall();

    //     //     this.txManager.sendTransaction(DefaultGasProvider.GAS_PRICE, BigInteger.valueOf(1000000),
    //     //         contract.getContractAddress(), rejectionCall, BigInteger.ZERO).getTransactionHash();
    //     // }
    // }

    @Override
    public void addPayment(Case monitoredCase, CasePayment payment, Boolean sync){
        //if (this.checkIfCaseExists(monitoredCase.getUuid())) {
            try {

                log.info("add new payment for case with uuid :{} and state :{}", monitoredCase.getUuid(), monitoredCase.getState().getValue());
                LocalDateTime time = monitoredCase.getDate();
                ZonedDateTime zdt = time.atZone(ZoneId.of("Europe/Athens"));
                long millis = zdt.toInstant().toEpochMilli();
                byte[] uuid = ByteConverters.stringToBytes16(monitoredCase.getUuid()).getValue();
                String functionCall = this.getContract()
                        .addPayment(uuid, BigInteger.valueOf(payment.getState().getValue()), 
                                BigInteger.valueOf(millis), payment.getPayment().multiply(BigDecimal.valueOf(100)).toBigInteger(),
                                payment.getCalculatedPayment().multiply(BigDecimal.valueOf(100)).toBigInteger(), 
                                monitoredCase.getOffset().multiply(BigDecimal.valueOf(100)).toBigInteger())
                        .encodeFunctionCall();
                this.txManager.sendTransaction(DefaultGasProvider.GAS_PRICE, BigInteger.valueOf(1000000),
                        contract.getContractAddress(), functionCall, BigInteger.ZERO).getTransactionHash();

                // if(sync){
                //     TransactionReceiptProcessor receiptProcessor = new PollingTransactionReceiptProcessor(
                //         web3, 
                //         TransactionManager.DEFAULT_POLLING_FREQUENCY, 
                //         TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH);
                //     TransactionReceipt txReceipt = receiptProcessor.waitForTransactionReceipt(txHash);
                // }
            } catch (IOException ex) {
                log.error(ex.getMessage());
            }
        // } else {
        //     log.error("no case found for uuid {}", monitoredCase.getUuid());
        // }
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

    @Override
    public Page<String> getCaseUuidsPaginated(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<String> uuidList = getAllCaseUUID();
        List<String> uuidSublist;

        if (uuidList.size() < startItem) {
            uuidSublist = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, uuidList.size());
            uuidSublist = uuidList.subList(startItem, toIndex);
        }

        Page<String> uuidPage = new PageImpl<String>(uuidSublist, PageRequest.of(currentPage, pageSize), uuidList.size());

        return uuidPage;
    }
}
