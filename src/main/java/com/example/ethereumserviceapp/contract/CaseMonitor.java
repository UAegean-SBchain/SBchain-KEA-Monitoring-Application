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
import org.web3j.tuples.generated.Tuple7;
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
    public static final String BINARY = "0x608060405234801561001057600080fd5b50610f0b806100206000396000f3fe608060405234801561001057600080fd5b50600436106100625760003560e01c806345b10ce414610067578063bb98be40146100bc578063d36cf4a3146101be578063d4988c15146102b9578063d840e71714610312578063fb40c22a146104b0575b600080fd5b6100a66004803603602081101561007d57600080fd5b8101908080356fffffffffffffffffffffffffffffffff1916906020019092919050505061050f565b6040518082815260200191505060405180910390f35b6101a8600480360360808110156100d257600080fd5b8101908080356fffffffffffffffffffffffffffffffff191690602001909291908035906020019064010000000081111561010c57600080fd5b82018360208201111561011e57600080fd5b8035906020019184600183028401116401000000008311171561014057600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f82011690508083019250505050505050919291929080351515906020019092919080359060200190929190505050610552565b6040518082815260200191505060405180910390f35b6102b7600480360360a08110156101d457600080fd5b8101908080356fffffffffffffffffffffffffffffffff191690602001909291908035906020019064010000000081111561020e57600080fd5b82018360208201111561022057600080fd5b8035906020019184600183028401116401000000008311171561024257600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f82011690508083019250505050505050919291929080351515906020019092919080359060200190929190803560ff1690602001909291905050506107d5565b005b6102f8600480360360208110156102cf57600080fd5b8101908080356fffffffffffffffffffffffffffffffff191690602001909291905050506108fe565b604051808215151515815260200191505060405180910390f35b6103516004803603602081101561032857600080fd5b8101908080356fffffffffffffffffffffffffffffffff19169060200190929190505050610986565b60405180886fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff19168152602001806020018715151515815260200186815260200180602001806020018560038111156103a957fe5b60ff16815260200184810384528a818151815260200191508051906020019080838360005b838110156103e95780820151818401526020810190506103ce565b50505050905090810190601f1680156104165780820380516001836020036101000a031916815260200191505b50848103835287818151815260200191508051906020019060200280838360005b83811015610452578082015181840152602081019050610437565b50505050905001848103825286818151815260200191508051906020019060200280838360005b83811015610494578082015181840152602081019050610479565b505050509050019a505050505050505050505060405180910390f35b6104b8610b99565b6040518080602001828103825283818151815260200191508051906020019060200280838360005b838110156104fb5780820151818401526020810190506104e0565b505050509050019250505060405180910390f35b600060016000836fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff19168152602001908152602001600020549050919050565b600061055d856108fe565b1561056757600080fd5b606060016040519080825280602002602001820160405280156105995781602001602082028038833980820191505090505b50905082816000815181106105aa57fe5b602002602001018181525050606060016040519080825280602002602001820160405280156105e85781602001602082028038833980820191505090505b5090506000816000815181106105fa57fe5b6020026020010190600381111561060d57fe5b9081600381111561061a57fe5b8152505060006040518060e00160405280896fffffffffffffffffffffffffffffffff1916815260200188815260200187151581526020018681526020018481526020018381526020016000600381111561067157fe5b8152509080600181540180825580915050906001820390600052602060002090600702016000909192909190915060008201518160000160006101000a8154816fffffffffffffffffffffffffffffffff021916908360801c021790555060208201518160010190805190602001906106eb929190610c85565b5060408201518160020160006101000a81548160ff021916908315150217905550606082015181600301556080820151816004019080519060200190610732929190610d05565b5060a082015181600501908051906020019061074f929190610d52565b5060c08201518160060160006101000a81548160ff0219169083600381111561077457fe5b02179055505050506000600160008054905003905080600160008a6fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff1916815260200190815260200160002081905550809350505050949350505050565b6107de856108fe565b6107e757600080fd5b60006107f28661050f565b9050600080828154811061080257fe5b906000526020600020906007020190508581600101908051906020019061082a929190610e01565b50848160020160006101000a81548160ff021916908315150217905550838160030181905550806004018490806001815401808255809150509060018203906000526020600020016000909192909190915055508060050183908060018154018082558091505090600182039060005260206000209060209182820401919006909192909190916101000a81548160ff021916908360038111156108ca57fe5b021790555050828160060160006101000a81548160ff021916908360038111156108f057fe5b021790555050505050505050565b60008060008054905014156109165760009050610981565b816fffffffffffffffffffffffffffffffff191660006109358461050f565b8154811061093f57fe5b906000526020600020906007020160000160009054906101000a900460801b6fffffffffffffffffffffffffffffffff191614156109805760019050610981565b5b919050565b60006060600080606080600061099b886108fe565b6109a457600080fd5b6000806109b08a61050f565b815481106109ba57fe5b906000526020600020906007020190508060000160009054906101000a900460801b816001018260020160009054906101000a900460ff16836003015484600401856005018660060160009054906101000a900460ff16858054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610aa65780601f10610a7b57610100808354040283529160200191610aa6565b820191906000526020600020905b815481529060010190602001808311610a8957829003601f168201915b5050505050955082805480602002602001604051908101604052809291908181526020018280548015610af857602002820191906000526020600020905b815481526020019060010190808311610ae4575b5050505050925081805480602002602001604051908101604052809291908181526020018280548015610b7857602002820191906000526020600020906000905b82829054906101000a900460ff166003811115610b5257fe5b81526020019060010190602082600001049283019260010382029150808411610b395790505b50505050509150975097509750975097509750975050919395979092949650565b606080600080549050604051908082528060200260200182016040528015610bd05781602001602082028038833980820191505090505b509050600080805490501115610c7e5760008090506000808054905090505b6000811115610c7b5760006001820381548110610c0857fe5b906000526020600020906007020160000160009054906101000a900460801b838380600101945081518110610c3957fe5b60200260200101906fffffffffffffffffffffffffffffffff191690816fffffffffffffffffffffffffffffffff191681525050808060019003915050610bef565b50505b8091505090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10610cc657805160ff1916838001178555610cf4565b82800160010185558215610cf4579182015b82811115610cf3578251825591602001919060010190610cd8565b5b509050610d019190610e81565b5090565b828054828255906000526020600020908101928215610d41579160200282015b82811115610d40578251825591602001919060010190610d25565b5b509050610d4e9190610e81565b5090565b82805482825590600052602060002090601f01602090048101928215610df05791602002820160005b83821115610dc157835183826101000a81548160ff02191690836003811115610da057fe5b02179055509260200192600101602081600001049283019260010302610d7b565b8015610dee5782816101000a81549060ff0219169055600101602081600001049283019260010302610dc1565b505b509050610dfd9190610ea6565b5090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10610e4257805160ff1916838001178555610e70565b82800160010185558215610e70579182015b82811115610e6f578251825591602001919060010190610e54565b5b509050610e7d9190610e81565b5090565b610ea391905b80821115610e9f576000816000905550600101610e87565b5090565b90565b610ed391905b80821115610ecf57600081816101000a81549060ff021916905550600101610eac565b5090565b9056fea265627a7a72315820dc7c1f9dc3e69d784ff42bb2550dbb1f825bde90012a13cc992238cab84c008464736f6c63430005100032";

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

    public RemoteFunctionCall<Tuple7<byte[], String, Boolean, BigInteger, List<BigInteger>, List<BigInteger>, BigInteger>> getCase(byte[] _uuid) {
        final Function function = new Function(FUNC_GETCASE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes16(_uuid)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes16>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Bool>() {}, new TypeReference<Uint256>() {}, new TypeReference<DynamicArray<Uint256>>() {}, new TypeReference<DynamicArray<Uint8>>() {}, new TypeReference<Uint8>() {}));
        return new RemoteFunctionCall<Tuple7<byte[], String, Boolean, BigInteger, List<BigInteger>, List<BigInteger>, BigInteger>>(function,
                new Callable<Tuple7<byte[], String, Boolean, BigInteger, List<BigInteger>, List<BigInteger>, BigInteger>>() {
                    @Override
                    public Tuple7<byte[], String, Boolean, BigInteger, List<BigInteger>, List<BigInteger>, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple7<byte[], String, Boolean, BigInteger, List<BigInteger>, List<BigInteger>, BigInteger>(
                                (byte[]) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (Boolean) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                convertToNative((List<Uint256>) results.get(4).getValue()), 
                                convertToNative((List<Uint8>) results.get(5).getValue()), 
                                (BigInteger) results.get(6).getValue());
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
