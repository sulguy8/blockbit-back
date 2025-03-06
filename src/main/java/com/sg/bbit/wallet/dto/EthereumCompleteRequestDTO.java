package com.sg.bbit.wallet.dto;

import lombok.Data;

@Data
public class EthereumCompleteRequestDTO {
    private PartialSignatureDTO firstSignature;
    private int secondParticipantIndex;
}