package com.sg.bbit.wallet.dto;

import lombok.Data;

@Data
public class BitcoinTransactionRequestDTO {
    private String fromAddress;
    private String toAddress;
    private long amountSatoshi;
    private String redeemScriptHex;
    private String privateKeyHex;
}
