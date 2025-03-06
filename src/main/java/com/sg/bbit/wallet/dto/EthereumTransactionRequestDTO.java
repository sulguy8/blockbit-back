package com.sg.bbit.wallet.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class EthereumTransactionRequestDTO {
    private String walletId;
    private int participantIndex;
    private String toAddress;
    private BigDecimal amount;
}