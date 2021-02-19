package com.example.ethereumserviceapp.contract;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import static org.web3j.tx.Contract.deployRemoteCall;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>
 * Auto generated code.
 * <p>
 * <strong>Do not modify!</strong>
 * <p>
 * Please use the <a href="https://docs.web3j.io/command_line.html">web3j
 * command line tools</a>, or the
 * org.web3j.codegen.SolidityFunctionWrapperGenerator in the
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen
 * module</a> to update.
 *
 * <p>
 * Generated with web3j version 4.5.11.
 */
@SuppressWarnings("rawtypes")
public class VcRevocationRegistry extends Contract {

    public static final String BINARY = "608060405234801561001057600080fd5b50610231806100206000396000f3fe608060405234801561001057600080fd5b50600436106100365760003560e01c80634294857f1461003b578063b75c7dc61461006c575b600080fd5b6100586004803603602081101561005157600080fd5b503561008b565b604080519115158252519081900360200190f35b6100896004803603602081101561008257600080fd5b50356100f7565b005b6000805415806100aa575060008281526002602052604090205460ff16155b156100b7575060006100f2565b600082815260016020526040812054815481106100d057fe5b906000526020600020906002020160010160019054906101000a900460ff1690505b919050565b604080516060810182528281526000602080830182815260018486018181528454918201855584805294517f290decd9548b62a8d60345a988386fc84ba6bc95484008f6362f93160ef3e56360029092029182015590517f290decd9548b62a8d60345a988386fc84ba6bc95484008f6362f93160ef3e5649091018054945160ff199095169115159190911761ff00191661010094151594909402939093179092555482518481529251600019919091019283927fcce4457a6b5b055f3f9745490a403041339565253dda111f084ecface757706c92918290030190a2600091825260016020818152604080852093909355600290529120805460ff1916909117905556fea265627a7a723158208b998d0c2aa1960d43758778d729a280587044db0de8fc70bf92215f1efd8bac64736f6c63430005100032";

    public static final String FUNC_ISREVOKED = "isRevoked";

    public static final String FUNC_REVOKE = "revoke";

    public static final Event REVOKEDEVENT_EVENT = new Event("RevokedEvent",
            Arrays.asList(new TypeReference<Uint256>(true) {
            }, new TypeReference<Bytes32>() {
            }));

    public static final Event LOG_EVENT = new Event("log",
            Arrays.asList(new TypeReference<Uint256>() {
            }, new TypeReference<Bytes32>() {
            }, new TypeReference<Bool>() {
            }));

    public static final Event LOGEXISTS_EVENT = new Event("logExists",
            Arrays.asList(new TypeReference<Bytes32>() {
            }, new TypeReference<Bool>() {
            }));

    @Deprecated
    protected VcRevocationRegistry(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected VcRevocationRegistry(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected VcRevocationRegistry(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected VcRevocationRegistry(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<RevokedEventEventResponse> getRevokedEventEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(REVOKEDEVENT_EVENT, transactionReceipt);
        ArrayList<RevokedEventEventResponse> responses = new ArrayList<RevokedEventEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RevokedEventEventResponse typedResponse = new RevokedEventEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.id = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.uuid = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<RevokedEventEventResponse> revokedEventEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, RevokedEventEventResponse>() {
            @Override
            public RevokedEventEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(REVOKEDEVENT_EVENT, log);
                RevokedEventEventResponse typedResponse = new RevokedEventEventResponse();
                typedResponse.log = log;
                typedResponse.id = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.uuid = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<RevokedEventEventResponse> revokedEventEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(REVOKEDEVENT_EVENT));
        return revokedEventEventFlowable(filter);
    }

    public List<LogEventResponse> getLogEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(LOG_EVENT, transactionReceipt);
        ArrayList<LogEventResponse> responses = new ArrayList<LogEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LogEventResponse typedResponse = new LogEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.id = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.uuid = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.exists = (Boolean) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<LogEventResponse> logEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, LogEventResponse>() {
            @Override
            public LogEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOG_EVENT, log);
                LogEventResponse typedResponse = new LogEventResponse();
                typedResponse.log = log;
                typedResponse.id = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.uuid = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.exists = (Boolean) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<LogEventResponse> logEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOG_EVENT));
        return logEventFlowable(filter);
    }

    public List<LogExistsEventResponse> getLogExistsEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(LOGEXISTS_EVENT, transactionReceipt);
        ArrayList<LogExistsEventResponse> responses = new ArrayList<LogExistsEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LogExistsEventResponse typedResponse = new LogExistsEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.uuid = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.exists = (Boolean) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<LogExistsEventResponse> logExistsEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, LogExistsEventResponse>() {
            @Override
            public LogExistsEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOGEXISTS_EVENT, log);
                LogExistsEventResponse typedResponse = new LogExistsEventResponse();
                typedResponse.log = log;
                typedResponse.uuid = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.exists = (Boolean) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<LogExistsEventResponse> logExistsEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGEXISTS_EVENT));
        return logExistsEventFlowable(filter);
    }

    public RemoteFunctionCall<Boolean> isRevoked(byte[] _uuid) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_ISREVOKED,
                Arrays.asList(new org.web3j.abi.datatypes.generated.Bytes32(_uuid)),
                Arrays.asList(new TypeReference<Bool>() {
                }));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> revoke(byte[] _uuid) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_REVOKE,
                Arrays.asList(new org.web3j.abi.datatypes.generated.Bytes32(_uuid)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static VcRevocationRegistry load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new VcRevocationRegistry(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static VcRevocationRegistry load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new VcRevocationRegistry(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static VcRevocationRegistry load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new VcRevocationRegistry(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static VcRevocationRegistry load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new VcRevocationRegistry(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<VcRevocationRegistry> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(VcRevocationRegistry.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<VcRevocationRegistry> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(VcRevocationRegistry.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<VcRevocationRegistry> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(VcRevocationRegistry.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<VcRevocationRegistry> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(VcRevocationRegistry.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static class RevokedEventEventResponse extends BaseEventResponse {

        public BigInteger id;

        public byte[] uuid;
    }

    public static class LogEventResponse extends BaseEventResponse {

        public BigInteger id;

        public byte[] uuid;

        public Boolean exists;
    }

    public static class LogExistsEventResponse extends BaseEventResponse {

        public byte[] uuid;

        public Boolean exists;
    }
}
