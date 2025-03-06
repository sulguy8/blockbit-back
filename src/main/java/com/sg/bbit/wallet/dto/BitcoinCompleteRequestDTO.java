package com.sg.bbit.wallet.dto;

import lombok.Data;

@Data
public class BitcoinCompleteRequestDTO {
    private PartiallySignedTransactionDTO partiallySignedTransaction;
    private String privateKeyHex;
}