/**
 * 
 */
package org.web3j.example;

import java.io.IOException;
import java.math.BigInteger;
import org.web3j.crypto.KlayCredentials;
import org.web3j.crypto.KlayRawTransaction;
import org.web3j.crypto.KlaytnTransactionEncoder;
import org.web3j.crypto.transaction.type.TxType;
import org.web3j.crypto.transaction.type.TxTypeSmartContractExecution;
import org.web3j.crypto.transaction.type.TxType.Type;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthChainId;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.klaytn.Web3j;
import org.web3j.utils.Numeric;

/**
 * 
 */
public class SmartContractExecutionExample {
	/**
	 * @param args
	 */
    
    void SmartContractExecutionExample(Web3j web3j, KlayCredentials credentials) throws IOException {

        BigInteger GAS_PRICE = BigInteger.valueOf(50000000000L);
        BigInteger GAS_LIMIT = BigInteger.valueOf(6721950);
        String from = credentials.getAddress();
        EthChainId EthchainId = web3j.ethChainId().send();
        long chainId = EthchainId.getChainId().longValue();
        String to = "0x000000000000000000000000000000000000dead";
        BigInteger nonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.LATEST).send().getTransactionCount();
        BigInteger value = BigInteger.valueOf(100);
        String data = "0x608060405234801561001057600080fd5b506";
        byte[] payload = data.getBytes();


        TxType.Type type = Type.SMART_CONTRACT_EXECUTION;


        KlayRawTransaction raw = KlayRawTransaction.createTransaction(
                        type,
                        nonce,
                        GAS_PRICE,
                        GAS_LIMIT,
                        to,
                        value,
                        from,
                        payload);

         byte[] signedMessage = KlaytnTransactionEncoder.signMessage(raw, chainId, credentials);
         String hexValue = Numeric.toHexString(signedMessage);
         EthSendTransaction transactionResponse = web3j.ethSendRawTransaction(hexValue).send();
         System.out.println(transactionResponse.getResult());
            
         TxTypeSmartContractExecution rawTransaction = TxTypeSmartContractExecution.decodeFromRawTransaction(signedMessage);


    }


}
