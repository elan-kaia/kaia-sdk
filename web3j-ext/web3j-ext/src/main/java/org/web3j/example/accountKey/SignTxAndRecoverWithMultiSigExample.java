package org.web3j.example.accountKey;

import org.web3j.tx.response.PollingTransactionReceiptProcessor;
import org.web3j.tx.response.TransactionReceiptProcessor;
import org.web3j.example.keySample;
import java.io.IOException;
import java.math.BigInteger;
import org.web3j.crypto.KaiaCredentials;
import org.web3j.crypto.KaiaRawTransaction;
import org.web3j.crypto.KaiaTransactionEncoder;
import org.web3j.crypto.transaction.type.TxType;
import org.web3j.crypto.transaction.type.TxType.Type;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthChainId;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.kaia.Web3j;
import org.web3j.protocol.kaia.core.method.response.KaiaRecoverFromTransactionResponse;
import org.web3j.utils.Numeric;
import org.web3j.protocol.core.methods.response.EthSendTransaction;

/**
 * 
 */
public class SignTxAndRecoverWithMultiSigExample implements keySample {
        /**
         * 
         */

        public static void run() throws Exception {
                Web3j web3j = Web3j.build(new HttpService(keySample.BAOBAB_URL));
                KaiaCredentials credentials1 = KaiaCredentials.create(keySample.MULTI_KEY_privkey1,
                                keySample.MULTI_KEY_address);
                KaiaCredentials credentials2 = KaiaCredentials.create(keySample.MULTI_KEY_privkey2,
                                keySample.MULTI_KEY_address);
                KaiaCredentials credentials3 = KaiaCredentials.create(keySample.MULTI_KEY_privkey3,
                                keySample.MULTI_KEY_address);

                BigInteger GAS_PRICE = BigInteger.valueOf(50000000000L);
                BigInteger GAS_LIMIT = BigInteger.valueOf(6721950);
                String from = credentials1.getAddress();
                EthChainId EthchainId = web3j.ethChainId().send();
                long chainId = EthchainId.getChainId().longValue();
                String to = "0x000000000000000000000000000000000000dead";
                BigInteger nonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.LATEST).send()
                                .getTransactionCount();
                BigInteger value = BigInteger.valueOf(100);

                TxType.Type type = Type.VALUE_TRANSFER;

                KaiaRawTransaction raw = KaiaRawTransaction.createTransaction(
                                type,
                                nonce,
                                GAS_PRICE,
                                GAS_LIMIT,
                                to,
                                value,
                                from);

                byte[] signedMessage = KaiaTransactionEncoder.signMessage(raw, chainId, credentials1);
                signedMessage = KaiaTransactionEncoder.signMessage(signedMessage, chainId, credentials2);
                signedMessage = KaiaTransactionEncoder.signMessage(signedMessage, chainId, credentials3);

                String hexValue = Numeric.toHexString(signedMessage);
                EthSendTransaction transactionResponse = web3j.ethSendRawTransaction(hexValue).send();
                System.out.println("TxHash : \n " + transactionResponse.getResult());

                String blockNumber = "latest";
                KaiaRecoverFromTransactionResponse response = web3j.kaiaRecoverFromTransaction(hexValue, blockNumber)
                                .send();
                System.out.println("Original address : " + from);
                System.out.println("Result address : " + response.getResult());

                web3j.shutdown();

        }

}
