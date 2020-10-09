/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.ethereumserviceapp;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.ethereumserviceapp.contract.CaseMonitor;
import com.example.ethereumserviceapp.utils.ByteConverters;

import org.junit.jupiter.api.Test;
import org.web3j.abi.datatypes.generated.Bytes16;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple8;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Numeric;

/**
 *
 * @author nikos
 */
public class TestWallet {

    @Test
    public void testGetBallance() throws IOException {
        Web3j web3 = Web3j.build(new HttpService("https://mainnet.infura.io/v3/051806cbbf204a4886f2ab400c2c20f9"));
        EthGetBalance balanceWei = web3
                .ethGetBalance("0xFa7892D5Faf243Ae1cF850f74454FF69655ea09F", DefaultBlockParameterName.LATEST).send();
        System.out.println("balance in wei: " + balanceWei.getResult());

    }

    @Test
    public void testWallet() throws IOException {
        Web3j web3 = Web3j.build(new HttpService("https://ropsten.infura.io/v3/051806cbbf204a4886f2ab400c2c20f9"));
        String password = null; // no encryption
        String mnemonic = "enlist addict era market spawn van medal despair melt shift sustain multiply";
        // Derivation path wanted: // m/44'/60'/0'/0 (this is used in ethereum, in
        // bitcoin it is different
        int[] derivationPath = { 44 | Bip32ECKeyPair.HARDENED_BIT, 60 | Bip32ECKeyPair.HARDENED_BIT,
                0 | Bip32ECKeyPair.HARDENED_BIT, 0, 0 };

        // Generate a BIP32 master keypair from the mnemonic phrase
        Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(MnemonicUtils.generateSeed(mnemonic, password));
        // Derived the key using the derivation path
        Bip32ECKeyPair derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, derivationPath);

        // Load the wallet for the derived key
        Credentials credentials = Credentials.create(derivedKeyPair);
        String accountAddress = credentials.getAddress();
        System.out.println("Account address: " + credentials.getAddress());

        EthGetBalance balanceWei = web3.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST)
                .send();
        System.out.println("balance in wei: " + balanceWei.getResult());

    }

    @Test
    public void testInvokeContractMethod() throws InterruptedException, ExecutionException, Exception {
        Web3j web3 = Web3j.build(new HttpService("https://ropsten.infura.io/v3/051806cbbf204a4886f2ab400c2c20f9"));
        String password = null; // no encryption
        String mnemonic = "enlist addict era market spawn van medal despair melt shift sustain multiply";
        // Derivation path wanted: // m/44'/60'/0'/0 (this is used in ethereum, in
        // bitcoin it is different
        int[] derivationPath = { 44 | Bip32ECKeyPair.HARDENED_BIT, 60 | Bip32ECKeyPair.HARDENED_BIT,
                0 | Bip32ECKeyPair.HARDENED_BIT, 0, 0 };

        // Generate a BIP32 master keypair from the mnemonic phrase
        Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(MnemonicUtils.generateSeed(mnemonic, password));
        // Derived the key using the derivation path
        Bip32ECKeyPair derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, derivationPath);

        // Load the wallet for the derived key
        Credentials credentials = Credentials.create(derivedKeyPair);

        CaseMonitor contract = CaseMonitor.load("0x3fF7e31E973E25071Db1E0c32B1e366f8aC5a265", web3, credentials,
                new DefaultGasProvider());

        Bytes16 uuidBytes = ByteConverters.stringToBytes16("fakeuuid");
        String functionCall = contract.addCase(uuidBytes.getValue(), BigInteger.valueOf(12313))
                .encodeFunctionCall();

        TransactionManager txManager = new FastRawTransactionManager(web3, credentials);

        String txHash = txManager.sendTransaction(DefaultGasProvider.GAS_PRICE, BigInteger.valueOf(1000000),
                contract.getContractAddress(), functionCall, BigInteger.ZERO).getTransactionHash();

        List cases = contract.getAllCases().sendAsync().get();
        cases.stream().forEach(obj -> {
            System.out.println(obj.toString());
        });

    }

    @Test
    public void testCallContractMethod() throws InterruptedException, ExecutionException, Exception {
        Web3j web3 = Web3j.build(new HttpService("https://ropsten.infura.io/v3/051806cbbf204a4886f2ab400c2c20f9"));
        String password = null; // no encryption
        String mnemonic = "enlist addict era market spawn van medal despair melt shift sustain multiply";
        // Derivation path wanted: // m/44'/60'/0'/0 (this is used in ethereum, in
        // bitcoin it is different
        int[] derivationPath = { 44 | Bip32ECKeyPair.HARDENED_BIT, 60 | Bip32ECKeyPair.HARDENED_BIT,
                0 | Bip32ECKeyPair.HARDENED_BIT, 0, 0 };

        // Generate a BIP32 master keypair from the mnemonic phrase
        Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(MnemonicUtils.generateSeed(mnemonic, password));
        // Derived the key using the derivation path
        Bip32ECKeyPair derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, derivationPath);

        // Load the wallet for the derived key
        Credentials credentials = Credentials.create(derivedKeyPair);

        CaseMonitor contract = CaseMonitor.load("0x3fF7e31E973E25071Db1E0c32B1e366f8aC5a265", web3, credentials,
                new DefaultGasProvider());

        List<byte[]> cases = contract.getAllCases().sendAsync().get();
        cases.stream().forEach(caseId -> {

            try {
                Tuple8<byte[], BigInteger, List<BigInteger>, List<BigInteger>, BigInteger, List<BigInteger>, List<BigInteger>,BigInteger> res = contract
                        .getCase((byte[]) caseId).sendAsync().get();
                System.out.println(res.component2());
                String dataInString = ByteConverters.hexToASCII(Numeric.toHexStringNoPrefix(res.component1()));
                System.out.println(dataInString);

            } catch (InterruptedException ex) {
                Logger.getLogger(TestWallet.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(TestWallet.class.getName()).log(Level.SEVERE, null, ex);
            }

        });

    }

    @Test
    public void testUtils() {
        String t = "123123";
        System.out.println(ByteConverters.asciiToHex32(t));

    }

}
