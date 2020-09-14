package com.example.ethereumserviceapp.service;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import com.example.ethereumserviceapp.contract.CaseMonitor;
import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.State;

import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple5;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ContractService {

    private static final String ACCOUNT_KEY = "9b47c9d425abd312b519d27519b64a1d2fe9200f40cf3581862fab65294b183a";
    private static final String CONTRACT_ADDRESS = "0xAC37921b87e187485F977e7d0D03Bf39E111E447";

    // Web3j web3j = Web3j.build(new
    // HttpService("https://ropsten.infura.io/v3/d1c7815f7c0a4f41b6abcd0c0cd141c5"));
    Web3j web3j = Web3j.build(new HttpService("http://127.0.0.1:7545"));
    String contractAddress = "0xAC37921b87e187485F977e7d0D03Bf39E111E447";
    
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

    public void deployContract() {
        Credentials credentials = Credentials.create(ACCOUNT_KEY);

        // Credentials credentials = Credentials
        // .create("6800b05cefcd45574fccaadb5cb807b4f18781887f73df4f110a5580bed0919e");

        // TransactionManager txManager = new FastRawTransactionManager(this.web3j,
        // credentials, processor);

        final BigInteger gasPrice = BigInteger.valueOf(2200000);
        final BigInteger gasLimit = BigInteger.valueOf(4300000);
        final ContractGasProvider gasProvider = new StaticGasProvider(gasPrice, gasLimit);
        // Throws org.web3j.protocol.exceptions.ClientConnectionException: Invalid
        // response received: 401; user project is inactive
        log.info("0000000000000000000000000000000 before deployment of contract ");
        CaseMonitor contract;
        try {
            contract = CaseMonitor.deploy(this.web3j, credentials, gasProvider).send();
            setContractAddress(contract.getContractAddress());
            log.info("1111111111111111111111111111111 after deployment contract :{}", contract);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    public void addCaseToBChain(String uuidStr, String caseName, Boolean isStudent, String date) {

        try {

            Credentials credentials = Credentials.create(ACCOUNT_KEY);

            final BigInteger gasPrice = BigInteger.valueOf(2200000);
            final BigInteger gasLimit = BigInteger.valueOf(4300000);

            final ContractGasProvider gasProvider = new StaticGasProvider(gasPrice, gasLimit);
            CaseMonitor contract = CaseMonitor.load(this.contractAddress, this.web3j, credentials, gasProvider);

            UUID uuid = UUID.fromString(uuidStr);

            // ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
            // bb.putLong(uuid.getMostSignificantBits());
            // bb.putLong(uuid.getLeastSignificantBits());
            

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            contract.addCase(/*bb.array()*/ uuidStr.getBytes(), caseName, isStudent, BigInteger.valueOf(sdf.parse(date).getTime())).send();

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void getAllCases() {
        Credentials credentials = Credentials.create(ACCOUNT_KEY);

        final BigInteger gasPrice = BigInteger.valueOf(2200000);
        final BigInteger gasLimit = BigInteger.valueOf(4300000);

        final ContractGasProvider gasProvider = new StaticGasProvider(gasPrice, gasLimit);
        CaseMonitor contract = CaseMonitor.load(this.contractAddress, this.web3j, credentials, gasProvider);

        try {
            List<byte[]> cases = contract.getAllCases().send();
            log.info("11111111111111111111111111111111 cases ?!? :{}", cases);

            for(byte[] caseId:cases){
                getCase(caseId, contract);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void updateCase(String uuidStr, String caseName, Boolean isStudent, String date, int state){
        try {

            Credentials credentials = Credentials.create(ACCOUNT_KEY);

            final BigInteger gasPrice = BigInteger.valueOf(2200000);
            final BigInteger gasLimit = BigInteger.valueOf(4300000);

            final ContractGasProvider gasProvider = new StaticGasProvider(gasPrice, gasLimit);
            CaseMonitor contract = CaseMonitor.load(CONTRACT_ADDRESS, this.web3j, credentials, gasProvider);

            UUID uuid = UUID.fromString(uuidStr);

            // ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
            // bb.putLong(uuid.getMostSignificantBits());
            // bb.putLong(uuid.getLeastSignificantBits());

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            contract.updateCase(/*bb.array()*/ uuidStr.getBytes(), caseName, isStudent, BigInteger.valueOf(sdf.parse(date).getTime()), BigInteger.valueOf(state)).send();

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void getCase(byte[] uuid, CaseMonitor contract) {
        try {
            Tuple5<byte[], String, Boolean, BigInteger, BigInteger> theCase = contract.getCase(uuid).send();

            log.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxx case :{}", theCase.toString());

            Case transformedCase = transformTuple(theCase);
            log.info("yyyyyyyyyyyyyyyyyyyyyyyyyyy case object :{}", transformedCase);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    private Case transformTuple(Tuple5<byte[], String, Boolean, BigInteger, BigInteger> theCase){

        Case transformedCase = new Case();

        ByteBuffer byteBuffer = ByteBuffer.wrap(theCase.component1());
        Long high = byteBuffer.getLong();
        Long low = byteBuffer.getLong();

        transformedCase.setUuid(String.valueOf(new UUID(high, low)));
        transformedCase.setName(theCase.component2());
        transformedCase.setIsStudent(theCase.component3());
        transformedCase.setDate(Instant.ofEpochMilli(theCase.component4().longValue()).atZone(ZoneId.systemDefault()).toLocalDate());
        
        transformedCase.setState(State.values()[theCase.component5().intValue()]);

        return transformedCase;
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

            // fileName = WalletUtils.generateNewWalletFile("password123", new
            // File("C:/dev/Ethereum/walletFileTest"));

            // Credentials credentials = WalletUtils.loadCredentials("password123",
            // "C:/dev/Ethereum/walletFileTest/" + fileName);
            Credentials credentials = Credentials.create(ACCOUNT_KEY);
            final BigInteger gasPrice = BigInteger.valueOf(2200000);
            final BigInteger gasLimit = BigInteger.valueOf(4300000);

            final ContractGasProvider gasProvider = new StaticGasProvider(gasPrice, gasLimit);
            CaseMonitor contract = CaseMonitor.load(CONTRACT_ADDRESS, web3j, credentials,
                    gasProvider);
            log.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxx contract address :{}", contract.getContractAddress());
            log.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxx contract getAllCases :{}", contract.getAllCases().toString());

            // Throws Invalid response received: 401; user project is inactive
            // TransactionReceipt transactionReceipt = contract
            // .addCase("uuid123", "testCase1", false,
            // BigInteger.valueOf(System.currentTimeMillis())).send();
            log.info("sssssssssssssssssssss");
            List<String> result = (List<String>) contract.getAllCases().send();
            log.info("5555555555555555555555555555 result :{}", result);
            // log.info("5555555555555555555555555555 transactionReceipt :{}",
            // transactionReceipt.toString());

        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    private void setContractAddress(String address){

        this.contractAddress = address;

    }

    // public void testTransaction() {

    //     final BigInteger gasPrice = BigInteger.valueOf(2200000);
    //     final BigInteger gasLimit = BigInteger.valueOf(4300000);

    //     EthGetTransactionCount ethGetTransactionCount;
    //     try {
    //         log.info("111111111111111111111111111111 raw transaction inside try/catch");
    //         ethGetTransactionCount = web3j.ethGetTransactionCount("0x3a69e747f6BEC341cDe8F10A996f4E2375b61879",
    //                 DefaultBlockParameterName.LATEST).sendAsync().get();
        
    //                 log.info("22222222222222222222222222222222 ");
    //     BigInteger nonce = ethGetTransactionCount.getTransactionCount();
    //     // using a raw transaction
    //     log.info("22222222222222222222222222222222 ");
    //     RawTransaction rawTransaction = RawTransaction.createContractTransaction(
    //         nonce,
    //         gasPrice,
    //         gasLimit,
    //         BigInteger.valueOf(0),
    //         "0x608060405234801561001057600080fd5b506109b2806100206000396000f3fe608060405234801561001057600080fd5b506004361061004c5760003560e01c8063487e19d514610051578063e4fe93f214610192578063edaab5b5146101d8578063fb40c22a14610340575b600080fd5b61007d6004803603602081101561006757600080fd5b810190808035906020019092919050505061039f565b604051808781526020018060200180602001861515151581526020018581526020018460038111156100ab57fe5b60ff168152602001838103835288818151815260200191508051906020019080838360005b838110156100eb5780820151818401526020810190506100d0565b50505050905090810190601f1680156101185780820380516001836020036101000a031916815260200191505b50838103825287818151815260200191508051906020019080838360005b83811015610151578082015181840152602081019050610136565b50505050905090810190601f16801561017e5780820380516001836020036101000a031916815260200191505b509850505050505050505060405180910390f35b6101be600480360360208110156101a857600080fd5b81019080803590602001909291905050506105a2565b604051808215151515815260200191505060405180910390f35b61033e600480360360808110156101ee57600080fd5b810190808035906020019064010000000081111561020b57600080fd5b82018360208201111561021d57600080fd5b8035906020019184600183028401116401000000008311171561023f57600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f820116905080830192505050505050509192919290803590602001906401000000008111156102a257600080fd5b8201836020820111156102b457600080fd5b803590602001918460018302840111640100000000831117156102d657600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f820116905080830192505050505050509192919290803515159060200190929190803590602001909291905050506105df565b005b610348610804565b6040518080602001828103825283818151815260200191508051906020019060200280838360005b8381101561038b578082015181840152602081019050610370565b505050509050019250505060405180910390f35b600060608060008060006103b2876105a2565b1561055d576000806103c3896108bb565b815481106103cd57fe5b90600052602060002090600602019050806000015481600101826002018360030160009054906101000a900460ff1684600401548560050160009054906101000a900460ff16848054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156104a85780601f1061047d576101008083540402835291602001916104a8565b820191906000526020600020905b81548152906001019060200180831161048b57829003601f168201915b50505050509450838054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156105445780601f1061051957610100808354040283529160200191610544565b820191906000526020600020905b81548152906001019060200180831161052757829003601f168201915b5050505050935096509650965096509650965050610599565b86600080600060405180602001604052806000815250929190604051806020016040528060008152509291908191509550955095509550955095505b91939550919395565b60008060008054905014156105ba57600090506105da565b600060016000848152602001908152602001600020549050600081119150505b919050565b6000848484846040516020018085805190602001908083835b6020831061061b57805182526020820191506020810190506020830392506105f8565b6001836020036101000a03801982511681845116808217855250505050505090500184805190602001908083835b6020831061066c5780518252602082019150602081019050602083039250610649565b6001836020036101000a038019825116818451168082178552505050505050905001831515151560f81b81526001018281526020019450505050506040516020818303038152906040528051906020012090506106c8816105a2565b156106d257600080fd5b60006040518060c0016040528083815260200187815260200186815260200185151581526020018481526020016000600381111561070c57fe5b815250908060018154018082558091505090600182039060005260206000209060060201600090919290919091506000820151816000015560208201518160010190805190602001906107609291906108d8565b50604082015181600201908051906020019061077d9291906108d8565b5060608201518160030160006101000a81548160ff0219169083151502179055506080820151816004015560a08201518160050160006101000a81548160ff021916908360038111156107cc57fe5b021790555050505060006001600080549050039050600181016001600084815260200190815260200160002081905550505050505050565b60608060008054905060405190808252806020026020018201604052801561083b5781602001602082028038833980820191505090505b5090506000808054905011156108b45760008090506000808054905090505b60008111156108b1576000600182038154811061087357fe5b90600052602060002090600602016000015483838060010194508151811061089757fe5b60200260200101818152505080806001900391505061085a565b50505b8091505090565b600060016000838152602001908152602001600020549050919050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061091957805160ff1916838001178555610947565b82800160010185558215610947579182015b8281111561094657825182559160200191906001019061092b565b5b5090506109549190610958565b5090565b61097a91905b8082111561097657600081600090555060010161095e565b5090565b9056fea265627a7a723158208179e50d0b48f82281bb74edbc5181ca28370273960f64864e8b67427aeea65064736f6c63430005100032");
    //     // send...

    //     log.info("hhhhhhhhhhhhhhhhhhhhhhhhh raw transaction :{}", rawTransaction);
    //     // get contract address
    //     EthGetTransactionReceipt transactionReceipt =
    //             web3j.ethGetTransactionReceipt("0xa2a37893ebb862a22af609b8d9feeaf8fecfdf28dd3ce8186337471b3bba39bf").send();

    //     if (transactionReceipt.getTransactionReceipt().isPresent()) {
    //         String contractAddress = transactionReceipt.getResult().getContractAddress();
    //     } else {
    //     // try again
    //     }
    //     } catch (Exception e) {
    //         log.error(e.getMessage());
    //     } 
    // }
}