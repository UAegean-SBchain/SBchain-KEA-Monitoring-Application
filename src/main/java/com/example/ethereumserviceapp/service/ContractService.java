// package com.example.ethereumserviceapp.service;

// import java.math.BigInteger;
// import java.nio.ByteBuffer;
// import java.text.SimpleDateFormat;
// import java.time.Instant;
// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.time.ZoneId;
// import java.time.format.DateTimeFormatter;
// import java.util.ArrayList;
// import java.util.LinkedHashMap;
// import java.util.List;
// import java.util.UUID;

// import com.example.ethereumserviceapp.contract.CaseMonitor;
// import com.example.ethereumserviceapp.model.Case;
// import com.example.ethereumserviceapp.model.State;
// import com.example.ethereumserviceapp.utils.ByteConverters;
// import com.example.ethereumserviceapp.utils.ContractBuilder;

// import org.springframework.stereotype.Service;
// import org.web3j.abi.datatypes.generated.Bytes16;
// import org.web3j.crypto.Bip32ECKeyPair;
// import org.web3j.crypto.Credentials;
// import org.web3j.crypto.MnemonicUtils;
// import org.web3j.protocol.Web3j;
// import org.web3j.protocol.http.HttpService;
// import org.web3j.tuples.generated.Tuple7;
// import org.web3j.tx.FastRawTransactionManager;
// import org.web3j.tx.TransactionManager;
// import org.web3j.tx.gas.ContractGasProvider;
// import org.web3j.tx.gas.DefaultGasProvider;
// import org.web3j.tx.gas.StaticGasProvider;

// import lombok.extern.slf4j.Slf4j;

// @Slf4j
// @Service
// public class ContractService {

//     //private static final String ACCOUNT_KEY = "24b26a6c3f3af6eb8b81e76c9c709548200af5d0f7f08480e9c391b01b036c56";

//     Web3j web3j = Web3j.build(new HttpService("https://ropsten.infura.io/v3/691797f6957f45e7944535265a9c13a6"));

//     String contractAddress = "0x59bc23a07c16ad163417056643dfd44a4b5d59b9";

//     // deploy contract to the block chain
//     // change .send() to .encodeFunctionCall() and create transaction as in addCase
//     public void deployContract() {
//         //Credentials credentials = Credentials.create(ACCOUNT_KEY);

//         // Credentials credentials = Credentials
//         // .create("6800b05cefcd45574fccaadb5cb807b4f18781887f73df4f110a5580bed0919e");

//         // TransactionManager txManager = new FastRawTransactionManager(this.web3j,
//         // credentials, processor);

//         final BigInteger gasPrice = BigInteger.valueOf(2200000);
//         final BigInteger gasLimit = BigInteger.valueOf(4300000);
//         final ContractGasProvider gasProvider = new StaticGasProvider(gasPrice, gasLimit);
//         // Throws org.web3j.protocol.exceptions.ClientConnectionException: Invalid
//         // response received: 401; user project is inactive

//         Credentials credentials = getCredentials();
//         CaseMonitor contract;
//         try {
//             contract = CaseMonitor.deploy(this.web3j, credentials, gasProvider).send();
//             setContractAddress(contract.getContractAddress());
//             log.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx contract address :{}", contract.getContractAddress());
//         } catch (Exception e) {
//             log.error(e.getMessage());
//         }

//     }

//     // Add case to the block chain
//     public void addCase(String uuidStr, String caseName, Boolean isStudent, String date) {

//         try {

//             // final BigInteger gasPrice = BigInteger.valueOf(2200000);
//             // final BigInteger gasLimit = BigInteger.valueOf(4300000);

//             // final ContractGasProvider gasProvider = new StaticGasProvider(gasPrice, gasLimit);

//             Credentials credentials = getCredentials();

//             CaseMonitor contract = CaseMonitor.load(this.contractAddress, this.web3j, credentials, new DefaultGasProvider());

//             // UUID uuid = UUID.fromString(uuidStr);
//             // ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
//             // bb.putLong(uuid.getMostSignificantBits());
//             // bb.putLong(uuid.getLeastSignificantBits());
            
//             SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//             Bytes16 uuidBytes = ByteConverters.stringToBytes16(uuidStr);

//             String functionCall = contract.addCase(/*bb.array()*/ uuidBytes.getValue(), caseName, isStudent, BigInteger.valueOf(sdf.parse(date).getTime())).encodeFunctionCall();

//             TransactionManager txManager = new FastRawTransactionManager(this.web3j, credentials);

//             String txHash = txManager.sendTransaction(DefaultGasProvider.GAS_PRICE, BigInteger.valueOf(1000000),
//                 contract.getContractAddress(), functionCall, BigInteger.ZERO).getTransactionHash();

//             log.info("transaction hash :{}", txHash);

//         } catch (Exception e) {
//             log.error(e.getMessage());
//         }
//     }

//     // retrives all cases from the block chain
//     public List<Case> getAllCases() {

//         List<Case> caseList = new ArrayList<>();

//         Credentials credentials = getCredentials();
//         CaseMonitor contract = CaseMonitor.load(this.contractAddress, this.web3j, credentials, new DefaultGasProvider());

//         try {
//             List<byte[]> cases = contract.getAllCases().send();

//             for(byte[] caseId:cases){
//                 caseList.add(getCase(caseId, contract));
//             }
//         } catch (Exception e) {
//             log.error(e.getMessage());
//         }

//         return caseList;
//     }

//     public void updateCase(String uuidStr, String caseName, Boolean isStudent, String date, int state){
//         try {

//             Credentials credentials = getCredentials();
//             CaseMonitor contract = CaseMonitor.load(this.contractAddress, this.web3j, credentials, new DefaultGasProvider());

//             SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//             Bytes16 uuidBytes = ByteConverters.stringToBytes16(uuidStr);

//             String functionCall = contract.updateCase(uuidBytes.getValue(), caseName, isStudent,
//                 BigInteger.valueOf(sdf.parse(date).getTime()), BigInteger.valueOf(state)).encodeFunctionCall();
           
//             TransactionManager txManager = new FastRawTransactionManager(this.web3j, credentials);

//             String txHash = txManager.sendTransaction(DefaultGasProvider.GAS_PRICE, BigInteger.valueOf(1000000),
//                 contract.getContractAddress(), functionCall, BigInteger.ZERO).getTransactionHash();

//             log.info("transaction hash :{}", txHash);


//         } catch (Exception e) {
//             log.error(e.getMessage());
//         }
//     }

//     private Case getCase(byte[] uuid, CaseMonitor contract) {
//         Case transformedCase = new Case();
//         try {
//             Tuple7<byte[], String, Boolean, BigInteger, List<BigInteger>, List<BigInteger>, BigInteger> theCase = contract.getCase(uuid).send();

//             transformedCase = ContractBuilder.buildCaseFromTuple(theCase);
//             log.info("case object :{}", transformedCase);
//         } catch (Exception e) {
//             log.error(e.getMessage());
//         }

//         return transformedCase;

//     }

//     private Case transformTuple(Tuple7<byte[], String, Boolean, BigInteger, List<BigInteger>, List<BigInteger>, BigInteger> theCase){

//         Case transformedCase = new Case();

//         LinkedHashMap<LocalDateTime, State> history = new LinkedHashMap<>();

//         ByteBuffer byteBuffer = ByteBuffer.wrap(theCase.component1());
//         Long high = byteBuffer.getLong();
//         Long low = byteBuffer.getLong();

//         transformedCase.setUuid(String.valueOf(new UUID(high, low)));
//         transformedCase.setName(theCase.component2());
//         transformedCase.setIsStudent(theCase.component3());
//         //transformedCase.setDate(Instant.ofEpochMilli(theCase.component4().longValue()).atZone(ZoneId.systemDefault()).toLocalDate());
        
//         transformedCase.setState(State.values()[theCase.component7().intValue()]);

//         for(int i=0; i<theCase.component5().size(); i++){
//             history.put(Instant.ofEpochMilli(theCase.component5().get(i).longValue()).atZone(ZoneId.systemDefault()).toLocalDateTime(), State.values()[theCase.component6().get(i).intValue()]);
//             transformedCase.setHistory(history);
//         }

//         return transformedCase;
//     }

//     private Credentials getCredentials(){
//         String password = null; // no encryption
//         String mnemonic = "heavy peace decline bean recall budget trigger video era trash also unveil";
//         //Derivation path wanted: // m/44'/60'/0'/0 (this is used in ethereum, in bitcoin it is different
//         int[] derivationPath = {44 | Bip32ECKeyPair.HARDENED_BIT, 60 | Bip32ECKeyPair.HARDENED_BIT, 0 | Bip32ECKeyPair.HARDENED_BIT, 0, 0};

//         // Generate a BIP32 master keypair from the mnemonic phrase
//         Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(MnemonicUtils.generateSeed(mnemonic, password));
//         // Derived the key using the derivation path
//         Bip32ECKeyPair derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, derivationPath);

//         // Load the wallet for the derived key
//         //Credentials credentials = Credentials.create(derivedKeyPair);

//         return Credentials.create(derivedKeyPair);
//     }

//     private void setContractAddress(String address){
//         this.contractAddress = address;
//     }

//     public void monitorCases(){
//         List<Case> caseList = getAllCases();

//         SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

//         for(Case theCase:caseList){

//             //TODO trigger external API call to update credentials
//             final Boolean isStudent = false; //mock call

//             //theCase.setIsStudent(isStudent);

//             if(!theCase.getIsStudent()){
//                 theCase.setState(State.ACCEPTED);
//             } else {
//                 theCase.setState(State.REJECTED);
//             }
//             String formattedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
//             updateCase(theCase.getUuid(), theCase.getName(), theCase.getIsStudent(), formattedDate, 1);
//         }
//     }
// }