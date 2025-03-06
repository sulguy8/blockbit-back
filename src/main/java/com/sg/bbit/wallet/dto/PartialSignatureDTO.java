package com.sg.bbit.wallet.dto;

import lombok.Data;

@Data
public class PartialSignatureDTO {
    private String walletId;
    private String transactionHash;
    private String partialSignature;
    private int participantIndex;
    private String rawTransaction;
}