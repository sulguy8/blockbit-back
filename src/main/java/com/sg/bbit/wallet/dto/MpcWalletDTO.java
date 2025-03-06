package com.sg.bbit.wallet.dto;

import lombok.Data;

@Data
public class MpcWalletDTO {
    private String walletId;  // MPC 세션 ID
    private String address;   // 이더리움 주소
    private String publicKey; // 집계된 공개키
}