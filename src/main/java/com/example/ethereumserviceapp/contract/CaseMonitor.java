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
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple6;
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
    public static final String BINARY = "0x608060405234801561001057600080fd5b506109cc806100206000396000f3fe608060405234801561001057600080fd5b506004361061004c5760003560e01c8063487e19d514610051578063e4fe93f214610192578063edaab5b5146101d8578063fb40c22a14610354575b600080fd5b61007d6004803603602081101561006757600080fd5b81019080803590602001909291905050506103b3565b604051808781526020018060200180602001861515151581526020018581526020018460038111156100ab57fe5b60ff168152602001838103835288818151815260200191508051906020019080838360005b838110156100eb5780820151818401526020810190506100d0565b50505050905090810190601f1680156101185780820380516001836020036101000a031916815260200191505b50838103825287818151815260200191508051906020019080838360005b83811015610151578082015181840152602081019050610136565b50505050905090810190601f16801561017e5780820380516001836020036101000a031916815260200191505b509850505050505050505060405180910390f35b6101be600480360360208110156101a857600080fd5b81019080803590602001909291905050506105b6565b604051808215151515815260200191505060405180910390f35b61033e600480360360808110156101ee57600080fd5b810190808035906020019064010000000081111561020b57600080fd5b82018360208201111561021d57600080fd5b8035906020019184600183028401116401000000008311171561023f57600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f820116905080830192505050505050509192919290803590602001906401000000008111156102a257600080fd5b8201836020820111156102b457600080fd5b803590602001918460018302840111640100000000831117156102d657600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f820116905080830192505050505050509192919290803515159060200190929190803590602001909291905050506105f3565b6040518082815260200191505060405180910390f35b61035c61081e565b6040518080602001828103825283818151815260200191508051906020019060200280838360005b8381101561039f578082015181840152602081019050610384565b505050509050019250505060405180910390f35b600060608060008060006103c6876105b6565b15610571576000806103d7896108d5565b815481106103e157fe5b90600052602060002090600602019050806000015481600101826002018360030160009054906101000a900460ff1684600401548560050160009054906101000a900460ff16848054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156104bc5780601f10610491576101008083540402835291602001916104bc565b820191906000526020600020905b81548152906001019060200180831161049f57829003601f168201915b50505050509450838054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156105585780601f1061052d57610100808354040283529160200191610558565b820191906000526020600020905b81548152906001019060200180831161053b57829003601f168201915b50505050509350965096509650965096509650506105ad565b86600080600060405180602001604052806000815250929190604051806020016040528060008152509291908191509550955095509550955095505b91939550919395565b60008060008054905014156105ce57600090506105ee565b600060016000848152602001908152602001600020549050600081119150505b919050565b600080858585856040516020018085805190602001908083835b60208310610630578051825260208201915060208101905060208303925061060d565b6001836020036101000a03801982511681845116808217855250505050505090500184805190602001908083835b60208310610681578051825260208201915060208101905060208303925061065e565b6001836020036101000a038019825116818451168082178552505050505050905001831515151560f81b81526001018281526020019450505050506040516020818303038152906040528051906020012090506106dd816105b6565b156106e757600080fd5b60006040518060c0016040528083815260200188815260200187815260200186151581526020018581526020016000600381111561072157fe5b815250908060018154018082558091505090600182039060005260206000209060060201600090919290919091506000820151816000015560208201518160010190805190602001906107759291906108f2565b5060408201518160020190805190602001906107929291906108f2565b5060608201518160030160006101000a81548160ff0219169083151502179055506080820151816004015560a08201518160050160006101000a81548160ff021916908360038111156107e157fe5b0217905550505050600060016000805490500390506001810160016000848152602001908152602001600020819055508192505050949350505050565b6060806000805490506040519080825280602002602001820160405280156108555781602001602082028038833980820191505090505b5090506000808054905011156108ce5760008090506000808054905090505b60008111156108cb576000600182038154811061088d57fe5b9060005260206000209060060201600001548383806001019450815181106108b157fe5b602002602001018181525050808060019003915050610874565b50505b8091505090565b600060016000838152602001908152602001600020549050919050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061093357805160ff1916838001178555610961565b82800160010185558215610961579182015b82811115610960578251825591602001919060010190610945565b5b50905061096e9190610972565b5090565b61099491905b80821115610990576000816000905550600101610978565b5090565b9056fea265627a7a723158203230b620ca3fa1da9517a976b0e5c94d3d5e1ca4698e0165bf3478efdc13fe3364736f6c63430005100032";

    public static final String FUNC_ADDCASE = "addCase";

    public static final String FUNC_CASEEXISTS = "caseExists";

    public static final String FUNC_GETALLCASES = "getAllCases";

    public static final String FUNC_GETCASE = "getCase";

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<String, String>();
        _addresses.put("5777", "0x132c3b39E5272D17C2193F8fA3BC973b9879f1E5");
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

    public RemoteFunctionCall<TransactionReceipt> addCase(String _uuid, String _caseName, Boolean _isStudent, BigInteger _date) {
        final Function function = new Function(
                FUNC_ADDCASE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_uuid), 
                new org.web3j.abi.datatypes.Utf8String(_caseName), 
                new org.web3j.abi.datatypes.Bool(_isStudent), 
                new org.web3j.abi.datatypes.generated.Uint256(_date)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> caseExists(byte[] _caseId) {
        final Function function = new Function(FUNC_CASEEXISTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_caseId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<List> getAllCases() {
        final Function function = new Function(FUNC_GETALLCASES, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Bytes32>>() {}));
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

    public RemoteFunctionCall<Tuple6<byte[], String, String, Boolean, BigInteger, BigInteger>> getCase(byte[] _caseId) {
        final Function function = new Function(FUNC_GETCASE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_caseId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Bool>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint8>() {}));
        return new RemoteFunctionCall<Tuple6<byte[], String, String, Boolean, BigInteger, BigInteger>>(function,
                new Callable<Tuple6<byte[], String, String, Boolean, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple6<byte[], String, String, Boolean, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple6<byte[], String, String, Boolean, BigInteger, BigInteger>(
                                (byte[]) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (String) results.get(2).getValue(), 
                                (Boolean) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue(), 
                                (BigInteger) results.get(5).getValue());
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
