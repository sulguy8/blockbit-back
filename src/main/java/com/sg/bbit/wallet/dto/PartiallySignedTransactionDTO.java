package com.sg.bbit.wallet.dto;

import lombok.Data;

@Data
public class PartiallySignedTransactionDTO {
    private String transactionHex;
    private String signatureHex;
    private String publicKeyHex;
    private String redeemScriptHex;
}