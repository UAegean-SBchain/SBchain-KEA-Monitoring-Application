package com.example.ethereumserviceapp.service;

import java.io.File;
import java.math.BigInteger;

import com.example.ethereumserviceapp.contract.CaseMonitor;

import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ContractService {

    Web3j web3j = Web3j.build(new HttpService("https://ropsten.infura.io/v3/d1c7815f7c0a4f41b6abcd0c0cd141c5"));
    // Web3ClientVersion web3ClientVersion = web3.web3ClientVersion().send();

    // String privatekey = "***********************************";
    // BigInteger privkey = new BigInteger(privatekey, 16);
    // ECKeyPair ecKeyPair = ECKeyPair.create(privkey);
    // Credentials credentials = Credentials.create(ecKeyPair);
    // NoOpProcessor processor = new NoOpProcessor(web3j);

    // Create and Init the default Web3J connection
    // public void customInit(String provider) {
    // this.web3j = Web3j.build(new HttpService(provider));
    // }

    public void addCaseToBChain() {

        try {

            //TODO Either create wallet once or import wallet somehow
            String fileName = WalletUtils.generateNewWalletFile("password123", new File("C:/dev/Ethereum/walletFileTest"));
            Credentials credentials = WalletUtils.loadCredentials("password123",
                    "C:/dev/Ethereum/walletFileTest/" + fileName);

            // TransactionManager txManager = new FastRawTransactionManager(this.web3j, credentials, processor);

            //Throws org.web3j.protocol.exceptions.ClientConnectionException: Invalid response received: 401; user project is inactive
            CaseMonitor contract = CaseMonitor
                    .deploy(this.web3j, credentials, new DefaultGasProvider()).send();

            log.info("1111111111111111111111111111111 after deployment contract :{}", contract);

            TransactionReceipt transactionReceipt = contract.addCase("uuid123", "testCase1", false, BigInteger.valueOf(System.currentTimeMillis())).send();
            log.info("2222222222222222222222222222222 transactionReceipt :{}", transactionReceipt.toString());

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public EthBlockNumber getBlockNumber() {
        EthBlockNumber result = new EthBlockNumber();
        try {
            result = this.web3j.ethBlockNumber().sendAsync().get();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return result;
    }

    public void testLoadContract() {
        String fileName;
        try {
            
            fileName = WalletUtils.generateNewWalletFile("password123", new File("C:/dev/Ethereum/walletFileTest"));

            Credentials credentials = WalletUtils.loadCredentials("password123", "C:/dev/Ethereum/walletFileTest/" + fileName);

            CaseMonitor contract = CaseMonitor.load("0x3a69e747f6BEC341cDe8F10A996f4E2375b61879", this.web3j, credentials, new DefaultGasProvider());
            log.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxx contract loaded :{}", contract);

            // Throws Invalid response received: 401; user project is inactive
            TransactionReceipt transactionReceipt = contract
                    .addCase("uuid123", "testCase1", false, BigInteger.valueOf(System.currentTimeMillis())).send();

            log.info("5555555555555555555555555555 transactionReceipt :{}", transactionReceipt.toString());
       
        } catch (Exception e) {
            log.error(e.getMessage());
        }
       
    }
}