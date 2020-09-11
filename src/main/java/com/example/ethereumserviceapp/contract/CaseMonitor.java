package com.example.ethereumserviceapp.contract;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes16;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple5;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.6.1.
 */
@SuppressWarnings("rawtypes")
public class CaseMonitor extends Contract {
    public static final String BINARY = "0x608060405234801561001057600080fd5b50610aed806100206000396000f3fe608060405234801561001057600080fd5b50600436106100625760003560e01c806345b10ce414610067578063bb98be40146100bc578063d36cf4a3146101be578063d4988c15146102b9578063d840e71714610312578063fb40c22a14610420575b600080fd5b6100a66004803603602081101561007d57600080fd5b8101908080356fffffffffffffffffffffffffffffffff1916906020019092919050505061047f565b6040518082815260200191505060405180910390f35b6101a8600480360360808110156100d257600080fd5b8101908080356fffffffffffffffffffffffffffffffff191690602001909291908035906020019064010000000081111561010c57600080fd5b82018360208201111561011e57600080fd5b8035906020019184600183028401116401000000008311171561014057600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f820116905080830192505050505050509192919290803515159060200190929190803590602001909291905050506104c2565b6040518082815260200191505060405180910390f35b6102b7600480360360a08110156101d457600080fd5b8101908080356fffffffffffffffffffffffffffffffff191690602001909291908035906020019064010000000081111561020e57600080fd5b82018360208201111561022057600080fd5b8035906020019184600183028401116401000000008311171561024257600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f82011690508083019250505050505050919291929080351515906020019092919080359060200190929190803560ff169060200190929190505050610646565b005b6102f8600480360360208110156102cf57600080fd5b8101908080356fffffffffffffffffffffffffffffffff191690602001909291905050506106ef565b604051808215151515815260200191505060405180910390f35b6103516004803603602081101561032857600080fd5b8101908080356fffffffffffffffffffffffffffffffff19169060200190929190505050610777565b60405180866fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff1916815260200180602001851515151581526020018481526020018360038111156103a157fe5b60ff168152602001828103825286818151815260200191508051906020019080838360005b838110156103e15780820151818401526020810190506103c6565b50505050905090810190601f16801561040e5780820380516001836020036101000a031916815260200191505b50965050505050505060405180910390f35b6104286108a7565b6040518080602001828103825283818151815260200191508051906020019060200280838360005b8381101561046b578082015181840152602081019050610450565b505050509050019250505060405180910390f35b600060016000836fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff19168152602001908152602001600020549050919050565b60006104cd856106ef565b156104d757600080fd5b60006040518060a00160405280876fffffffffffffffffffffffffffffffff1916815260200186815260200185151581526020018481526020016000600381111561051e57fe5b8152509080600181540180825580915050906001820390600052602060002090600502016000909192909190915060008201518160000160006101000a8154816fffffffffffffffffffffffffffffffff021916908360801c02179055506020820151816001019080519060200190610598929190610993565b5060408201518160020160006101000a81548160ff0219169083151502179055506060820151816003015560808201518160040160006101000a81548160ff021916908360038111156105e757fe5b0217905550505050600060016000805490500390508060016000886fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff191681526020019081526020016000208190555080915050949350505050565b61064f856106ef565b61065857600080fd5b60006106638661047f565b9050600080828154811061067357fe5b906000526020600020906005020190508581600101908051906020019061069b929190610a13565b50848160020160006101000a81548160ff021916908315150217905550838160030181905550828160040160006101000a81548160ff021916908360038111156106e157fe5b021790555050505050505050565b60008060008054905014156107075760009050610772565b816fffffffffffffffffffffffffffffffff191660006107268461047f565b8154811061073057fe5b906000526020600020906005020160000160009054906101000a900460801b6fffffffffffffffffffffffffffffffff191614156107715760019050610772565b5b919050565b600060606000806000610789866106ef565b61079257600080fd5b60008061079e8861047f565b815481106107a857fe5b906000526020600020906005020190508060000160009054906101000a900460801b816001018260020160009054906101000a900460ff1683600301548460040160009054906101000a900460ff16838054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561088c5780601f106108615761010080835404028352916020019161088c565b820191906000526020600020905b81548152906001019060200180831161086f57829003601f168201915b50505050509350955095509550955095505091939590929450565b6060806000805490506040519080825280602002602001820160405280156108de5781602001602082028038833980820191505090505b50905060008080549050111561098c5760008090506000808054905090505b6000811115610989576000600182038154811061091657fe5b906000526020600020906005020160000160009054906101000a900460801b83838060010194508151811061094757fe5b60200260200101906fffffffffffffffffffffffffffffffff191690816fffffffffffffffffffffffffffffffff1916815250508080600190039150506108fd565b50505b8091505090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106109d457805160ff1916838001178555610a02565b82800160010185558215610a02579182015b82811115610a015782518255916020019190600101906109e6565b5b509050610a0f9190610a93565b5090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10610a5457805160ff1916838001178555610a82565b82800160010185558215610a82579182015b82811115610a81578251825591602001919060010190610a66565b5b509050610a8f9190610a93565b5090565b610ab591905b80821115610ab1576000816000905550600101610a99565b5090565b9056fea265627a7a723158208ed0ba47cf48236a7d6fb684fecd8a736a674e57019f35b7336136c5af67613764736f6c63430005100032";

    public static final String FUNC__GETCASEINDEX = "_getCaseIndex";

    public static final String FUNC_ADDCASE = "addCase";

    public static final String FUNC_UPDATECASE = "updateCase";

    public static final String FUNC_CASEEXISTS = "caseExists";

    public static final String FUNC_GETALLCASES = "getAllCases";

    public static final String FUNC_GETCASE = "getCase";

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<String, String>();
        _addresses.put("3", "0x3a69e747f6BEC341cDe8F10A996f4E2375b61879");
        _addresses.put("5777", "0x05FA7e50Ba5EE9E4324014B46a475CCCC1d85875");
    }

    @Deprecated
    protected CaseMonitor(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected CaseMonitor(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected CaseMonitor(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected CaseMonitor(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<BigInteger> _getCaseIndex(byte[] _uuid) {
        final Function function = new Function(FUNC__GETCASEINDEX, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes16(_uuid)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> addCase(byte[] _uuid, String _caseName, Boolean _isStudent, BigInteger _date) {
        final Function function = new Function(
                FUNC_ADDCASE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes16(_uuid), 
                new org.web3j.abi.datatypes.Utf8String(_caseName), 
                new org.web3j.abi.datatypes.Bool(_isStudent), 
                new org.web3j.abi.datatypes.generated.Uint256(_date)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> updateCase(byte[] _uuid, String _caseName, Boolean _isStudent, BigInteger _date, BigInteger _state) {
        final Function function = new Function(
                FUNC_UPDATECASE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes16(_uuid), 
                new org.web3j.abi.datatypes.Utf8String(_caseName), 
                new org.web3j.abi.datatypes.Bool(_isStudent), 
                new org.web3j.abi.datatypes.generated.Uint256(_date), 
                new org.web3j.abi.datatypes.generated.Uint8(_state)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> caseExists(byte[] _uuid) {
        final Function function = new Function(FUNC_CASEEXISTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes16(_uuid)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<List> getAllCases() {
        final Function function = new Function(FUNC_GETALLCASES, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Bytes16>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<Tuple5<byte[], String, Boolean, BigInteger, BigInteger>> getCase(byte[] _uuid) {
        final Function function = new Function(FUNC_GETCASE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes16(_uuid)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes16>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Bool>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint8>() {}));
        return new RemoteFunctionCall<Tuple5<byte[], String, Boolean, BigInteger, BigInteger>>(function,
                new Callable<Tuple5<byte[], String, Boolean, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple5<byte[], String, Boolean, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple5<byte[], String, Boolean, BigInteger, BigInteger>(
                                (byte[]) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (Boolean) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue());
                    }
                });
    }

    @Deprecated
    public static CaseMonitor load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new CaseMonitor(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static CaseMonitor load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new CaseMonitor(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static CaseMonitor load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new CaseMonitor(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static CaseMonitor load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new CaseMonitor(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<CaseMonitor> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(CaseMonitor.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<CaseMonitor> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(CaseMonitor.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<CaseMonitor> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(CaseMonitor.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<CaseMonitor> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(CaseMonitor.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    protected String getStaticDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static String getPreviouslyDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }
}
