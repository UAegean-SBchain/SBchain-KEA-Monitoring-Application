package com.example.sbchainssioicdoauth2.contracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
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
 * <p>Generated with web3j version 4.5.11.
 */
@SuppressWarnings("rawtypes")
public class CaseMonitor extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b50610d56806100206000396000f3fe608060405234801561001057600080fd5b506004361061007d5760003560e01c8063d36cf4a31161005b578063d36cf4a3146101a2578063d4988c1514610269578063d840e717146102a4578063fb40c22a146104035761007d565b8063235f4c121461008257806345b10ce4146100ab578063bb98be40146100e4575b600080fd5b6100a96004803603602081101561009857600080fd5b50356001600160801b03191661045b565b005b6100d2600480360360208110156100c157600080fd5b50356001600160801b0319166104f3565b60408051918252519081900360200190f35b6100d2600480360360808110156100fa57600080fd5b6001600160801b0319823516919081019060408101602082013564010000000081111561012657600080fd5b82018360208201111561013857600080fd5b8035906020019184600183028401116401000000008311171561015a57600080fd5b91908080601f01602080910402602001604051908101604052809392919081815260200183838082843760009201919091525092955050505080351515915060200135610513565b6100a9600480360360a08110156101b857600080fd5b6001600160801b031982351691908101906040810160208201356401000000008111156101e457600080fd5b8201836020820111156101f657600080fd5b8035906020019184600183028401116401000000008311171561021857600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295505050508035151591506020810135906040013560ff1661070c565b6102906004803603602081101561027f57600080fd5b50356001600160801b031916610805565b604080519115158252519081900360200190f35b6102cb600480360360208110156102ba57600080fd5b50356001600160801b031916610861565b604080516001600160801b0319891681528615159181019190915260608101859052602081016080820160a0830160c0840185600381111561030957fe5b60ff16815260200184810384528a818151815260200191508051906020019080838360005b8381101561034657818101518382015260200161032e565b50505050905090810190601f1680156103735780820380516001836020036101000a031916815260200191505b508481038352875181528751602091820191808a01910280838360005b838110156103a8578181015183820152602001610390565b50505050905001848103825286818151815260200191508051906020019060200280838360005b838110156103e75781810151838201526020016103cf565b505050509050019a505050505050505050505060405180910390f35b61040b610a50565b60408051602080825283518183015283519192839290830191858101910280838360005b8381101561044757818101518382015260200161042f565b505050509050019250505060405180910390f35b61046481610805565b61046d57600080fd5b6000610478826104f3565b90506000818154811061048757fe5b60009182526020822060079091020180546001600160801b0319168155906104b26001830182610afd565b60028201805460ff191690556000600383018190556104d5906004840190610b44565b6104e3600583016000610b62565b50600601805460ff191690555050565b6001600160801b031981166000908152600160205260409020545b919050565b600061051e85610805565b1561052857600080fd5b60408051600180825281830190925260609160208083019080388339019050509050828160008151811061055857fe5b602090810291909101015260408051600180825281830190925260609181602001602082028038833901905050905060008160008151811061059657fe5b602002602001019060038111156105a957fe5b908160038111156105b657fe5b8152505060006040518060e00160405280896fffffffffffffffffffffffffffffffff1916815260200188815260200187151581526020018681526020018481526020018381526020016000600381111561060d57fe5b9052815460018082018085556000948552602094859020845160079094020180546001600160801b03191660809490941c939093178355838501518051919561065b93850192910190610b87565b50604082015160028201805460ff19169115159190911790556060820151600382015560808201518051610699916004840191602090910190610c05565b5060a082015180516106b5916005840191602090910190610c3f565b5060c082015160068201805460ff191660018360038111156106d357fe5b021790555050600080546001600160801b03198b1682526001602052604090912060001990910190819055945050505050949350505050565b61071585610805565b61071e57600080fd5b6000610729866104f3565b9050600080828154811061073957fe5b9060005260206000209060070201905085816001019080519060200190610761929190610b87565b5060028101805486151560ff1990911617905560038082018590556004820180546001818101835560009283526020808420909201889055600585018054918201808255908452928290209181049091018054929387939192601f166101000a60ff810219909216919084908111156107d657fe5b02179055505060068101805484919060ff191660018360038111156107f757fe5b021790555050505050505050565b600080546108155750600061050e565b6001600160801b03198216600061082b846104f3565b8154811061083557fe5b600091825260209091206007909102015460801b6001600160801b031916141561050e5750600161050e565b60006060600080606080600061087688610805565b61087f57600080fd5b60008061088b8a6104f3565b8154811061089557fe5b600091825260209182902060079190910201805460028083015460038401546006850154600180870180546040805161010094831615949094026000190190911696909604601f81018a90048a0283018a0190965285825296985060809590951b9660ff93841695929460048a019460058b0194931692918891908301828280156109615780601f1061093657610100808354040283529160200191610961565b820191906000526020600020905b81548152906001019060200180831161094457829003601f168201915b50505050509550828054806020026020016040519081016040528092919081815260200182805480156109b357602002820191906000526020600020905b81548152602001906001019080831161099f575b5050505050925081805480602002602001604051908101604052809291908181526020018280548015610a2f57602002820191906000526020600020906000905b82829054906101000a900460ff166003811115610a0d57fe5b8152602060019283018181049485019490930390920291018084116109f45790505b50505050509150975097509750975097509750975050919395979092949650565b606080600080549050604051908082528060200260200182016040528015610a82578160200160208202803883390190505b5060005490915015610af757600080545b8015610af45760006001820381548110610aa957fe5b60009182526020909120600790910201548351600184019360809290921b9185918110610ad257fe5b6001600160801b03199092166020928302919091019091015260001901610a93565b50505b90505b90565b50805460018160011615610100020316600290046000825580601f10610b235750610b41565b601f016020900490600052602060002090810190610b419190610ce9565b50565b5080546000825590600052602060002090810190610b419190610ce9565b50805460008255601f016020900490600052602060002090810190610b419190610ce9565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10610bc857805160ff1916838001178555610bf5565b82800160010185558215610bf5579182015b82811115610bf5578251825591602001919060010190610bda565b50610c01929150610ce9565b5090565b828054828255906000526020600020908101928215610bf55791602002820182811115610bf5578251825591602001919060010190610bda565b82805482825590600052602060002090601f01602090048101928215610cdd5791602002820160005b83821115610cae57835183826101000a81548160ff02191690836003811115610c8d57fe5b02179055509260200192600101602081600001049283019260010302610c68565b8015610cdb5782816101000a81549060ff0219169055600101602081600001049283019260010302610cae565b505b50610c01929150610d03565b610afa91905b80821115610c015760008155600101610cef565b610afa91905b80821115610c0157805460ff19168155600101610d0956fea265627a7a72315820da766f806ce9b5aab925c1877c07f93e1c03183bc66a516b77dfc9d5b7e55b5064736f6c63430005100032";

    public static final String FUNC__GETCASEINDEX = "_getCaseIndex";

    public static final String FUNC_ADDCASE = "addCase";

    public static final String FUNC_CASEEXISTS = "caseExists";

    public static final String FUNC_DELETECASE = "deleteCase";

    public static final String FUNC_GETALLCASES = "getAllCases";

    public static final String FUNC_GETCASE = "getCase";

    public static final String FUNC_UPDATECASE = "updateCase";

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

    public RemoteFunctionCall<Boolean> caseExists(byte[] _uuid) {
        final Function function = new Function(FUNC_CASEEXISTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes16(_uuid)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> deleteCase(byte[] _uuid) {
        final Function function = new Function(
                FUNC_DELETECASE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes16(_uuid)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
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
}
