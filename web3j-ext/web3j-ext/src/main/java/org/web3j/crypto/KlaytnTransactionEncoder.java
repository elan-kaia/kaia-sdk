/*
 * Copyright 2019 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.web3j.crypto;

import org.web3j.crypto.transaction.fee.FeePayer;
import org.web3j.crypto.transaction.type.AbstractTxType;
import org.web3j.crypto.transaction.type.TxType;
import org.web3j.crypto.transaction.type.TxType.Type;
import org.web3j.crypto.transaction.type.TxTypeAccountUpdate;
import org.web3j.crypto.transaction.type.TxTypeCancel;
import org.web3j.crypto.transaction.type.TxTypeFeeDelegate;
import org.web3j.crypto.transaction.type.TxTypeFeeDelegatedCancel;
import org.web3j.crypto.transaction.type.TxTypeFeeDelegatedCancelWithRatio;
import org.web3j.crypto.transaction.type.TxTypeFeeDelegatedSmartContractDeploy;
import org.web3j.crypto.transaction.type.TxTypeFeeDelegatedSmartContractDeployWithRatio;
import org.web3j.crypto.transaction.type.TxTypeFeeDelegatedSmartContractExecution;
import org.web3j.crypto.transaction.type.TxTypeFeeDelegatedSmartContractExecutionWithRatio;
import org.web3j.crypto.transaction.type.TxTypeFeeDelegatedValueTransfer;
import org.web3j.crypto.transaction.type.TxTypeFeeDelegatedValueTransferMemo;
import org.web3j.crypto.transaction.type.TxTypeFeeDelegatedValueTransferMemoWithRatio;
import org.web3j.crypto.transaction.type.TxTypeFeeDelegatedValueTransferWithRatio;
import org.web3j.crypto.transaction.type.TxTypeSmartContractDeploy;
import org.web3j.crypto.transaction.type.TxTypeSmartContractExecution;
import org.web3j.crypto.transaction.type.TxTypeValueTransfer;
import org.web3j.crypto.transaction.type.TxTypeValueTransferMemo;
import org.web3j.utils.KlayTransactionUtils;

/**
 * Create RLP encoded transaction, implementation as per p4 of the <a
 * href="http://gavwood.com/paper.pdf">yellow paper</a>.
 */
public class KlaytnTransactionEncoder extends TransactionEncoder{

    public static byte[] signMessage(KlayRawTransaction rawTransaction, long chainId, KlayCredentials credentials) {
        AbstractTxType tx = (AbstractTxType) rawTransaction.getTransaction();

        if (Type.isFeeDelegated(tx.getKlayType()) || Type.isPartialFeeDelegated(tx.getKlayType())) {
            TxTypeFeeDelegate senderTx = (TxTypeFeeDelegate) rawTransaction.getTransaction();
            return senderTx.sign(credentials, chainId).getRaw();
        }
        return tx.sign(credentials, chainId).getRaw();
    }

    public static byte[] signMessageAsFeePayer(KlayRawTransaction rawTransaction, long chainId,
            KlayCredentials credentials) {
        TxTypeFeeDelegate senderTx = (TxTypeFeeDelegate) rawTransaction.getTransaction();
        senderTx.setFeePayer(credentials.getAddress());
        KlayRawTransaction payerTx = new FeePayer(credentials, chainId).sign(senderTx);
        return payerTx.getRaw();
    }

    public static byte[] signMessage(byte[] encodedSenderTransaction, long chainId, KlayCredentials credentials) {

        TxType.Type type = KlayTransactionUtils.getType(encodedSenderTransaction);
        if (!Type.isFeeDelegated(type) && !Type.isPartialFeeDelegated(type)) {
            AbstractTxType encodedTx;
            switch (type) {
                case ACCOUNT_UPDATE:
                    encodedTx = TxTypeAccountUpdate
                            .decodeFromRawTransaction(encodedSenderTransaction);
                    break;
                case CANCEL:
                    encodedTx = TxTypeCancel
                            .decodeFromRawTransaction(encodedSenderTransaction);
                    break;
                case SMART_CONTRACT_DEPLOY:
                    encodedTx = TxTypeSmartContractDeploy
                            .decodeFromRawTransaction(encodedSenderTransaction);
                    break;
                case SMART_CONTRACT_EXECUTION:
                    encodedTx = TxTypeSmartContractExecution
                            .decodeFromRawTransaction(encodedSenderTransaction);
                    break;
                case VALUE_TRANSFER:
                    encodedTx = TxTypeValueTransfer
                            .decodeFromRawTransaction(encodedSenderTransaction);
                    break;
                case VALUE_TRANSFER_MEMO:
                    encodedTx = TxTypeValueTransferMemo
                            .decodeFromRawTransaction(encodedSenderTransaction);
                    break;
                default:
                    encodedTx = TxTypeValueTransfer
                            .decodeFromRawTransaction(encodedSenderTransaction);
                    break;
            }
            return encodedTx.sign(credentials, chainId).getRaw();
        }

        else {
            TxTypeFeeDelegate encodedTx;
            switch (type) {
                case FEE_DELEGATED_CANCEL:
                    encodedTx = TxTypeFeeDelegatedCancel
                            .decodeFromRawTransaction(encodedSenderTransaction);
                    break;
                case FEE_DELEGATED_CANCEL_WITH_RATIO:
                    encodedTx = TxTypeFeeDelegatedCancelWithRatio
                            .decodeFromRawTransaction(encodedSenderTransaction);
                    break;
                case FEE_DELEGATED_SMART_CONTRACT_DEPLOY:
                    encodedTx = TxTypeFeeDelegatedSmartContractDeploy
                            .decodeFromRawTransaction(encodedSenderTransaction);
                    break;
                case FEE_DELEGATED_SMART_CONTRACT_DEPLOY_WITH_RATIO:
                    encodedTx = TxTypeFeeDelegatedSmartContractDeployWithRatio
                            .decodeFromRawTransaction(encodedSenderTransaction);
                    break;
                case FEE_DELEGATED_SMART_CONTRACT_EXECUTION:
                    encodedTx = TxTypeFeeDelegatedSmartContractExecution
                            .decodeFromRawTransaction(encodedSenderTransaction);
                    break;
                case FEE_DELEGATED_SMART_CONTRACT_EXECUTION_WITH_RATIO:
                    encodedTx = TxTypeFeeDelegatedSmartContractExecutionWithRatio
                            .decodeFromRawTransaction(encodedSenderTransaction);
                    break;
                case FEE_DELEGATED_VALUE_TRANSFER:
                    encodedTx = TxTypeFeeDelegatedValueTransfer
                            .decodeFromRawTransaction(encodedSenderTransaction);
                    break;
                case FEE_DELEGATED_VALUE_TRANSFER_MEMO:
                    encodedTx = TxTypeFeeDelegatedValueTransferMemo
                            .decodeFromRawTransaction(encodedSenderTransaction);
                    break;
                case FEE_DELEGATED_VALUE_TRANSFER_WITH_RATIO:
                    encodedTx = TxTypeFeeDelegatedValueTransferWithRatio
                            .decodeFromRawTransaction(encodedSenderTransaction);
                    break;
                case FEE_DELEGATED_VALUE_TRANSFER_MEMO_WITH_RATIO:
                    encodedTx = TxTypeFeeDelegatedValueTransferMemoWithRatio
                            .decodeFromRawTransaction(encodedSenderTransaction);
                    break;
                default:
                    encodedTx = TxTypeFeeDelegatedValueTransfer
                            .decodeFromRawTransaction(encodedSenderTransaction);
                    break;
            }
            return encodedTx.sign(credentials, chainId).getRaw();
        }
    }

    public static byte[] signMessageAsFeePayer(byte[] encodedSenderTransaction, long chainId,
            KlayCredentials credentials) {

        TxType.Type type = KlayTransactionUtils.getType(encodedSenderTransaction);
        TxTypeFeeDelegate senderTx;
        switch (type) {
            case FEE_DELEGATED_CANCEL:
                senderTx = TxTypeFeeDelegatedCancel
                        .decodeFromRawTransaction(encodedSenderTransaction);
                break;
            case FEE_DELEGATED_CANCEL_WITH_RATIO:
                senderTx = TxTypeFeeDelegatedCancelWithRatio
                        .decodeFromRawTransaction(encodedSenderTransaction);
                break;
            case FEE_DELEGATED_SMART_CONTRACT_DEPLOY:
                senderTx = TxTypeFeeDelegatedSmartContractDeploy
                        .decodeFromRawTransaction(encodedSenderTransaction);
                break;
            case FEE_DELEGATED_SMART_CONTRACT_DEPLOY_WITH_RATIO:
                senderTx = TxTypeFeeDelegatedSmartContractDeployWithRatio
                        .decodeFromRawTransaction(encodedSenderTransaction);
                break;
            case FEE_DELEGATED_SMART_CONTRACT_EXECUTION:
                senderTx = TxTypeFeeDelegatedSmartContractExecution
                        .decodeFromRawTransaction(encodedSenderTransaction);
                break;
            case FEE_DELEGATED_SMART_CONTRACT_EXECUTION_WITH_RATIO:
                senderTx = TxTypeFeeDelegatedSmartContractExecutionWithRatio
                        .decodeFromRawTransaction(encodedSenderTransaction);
                break;
            case FEE_DELEGATED_VALUE_TRANSFER:
                senderTx = TxTypeFeeDelegatedValueTransfer
                        .decodeFromRawTransaction(encodedSenderTransaction);
                break;
            case FEE_DELEGATED_VALUE_TRANSFER_MEMO:
                senderTx = TxTypeFeeDelegatedValueTransferMemo
                        .decodeFromRawTransaction(encodedSenderTransaction);
                break;
            case FEE_DELEGATED_VALUE_TRANSFER_WITH_RATIO:
                senderTx = TxTypeFeeDelegatedValueTransferWithRatio
                        .decodeFromRawTransaction(encodedSenderTransaction);
                break;
            case FEE_DELEGATED_VALUE_TRANSFER_MEMO_WITH_RATIO:
                senderTx = TxTypeFeeDelegatedValueTransferMemoWithRatio
                        .decodeFromRawTransaction(encodedSenderTransaction);
                break;
            default:
                senderTx = TxTypeFeeDelegatedValueTransfer
                        .decodeFromRawTransaction(encodedSenderTransaction);
                break;
        }
        KlayRawTransaction payerTx = new FeePayer(credentials, chainId).sign(senderTx);
        ;
        return payerTx.getRaw();
    }

}
