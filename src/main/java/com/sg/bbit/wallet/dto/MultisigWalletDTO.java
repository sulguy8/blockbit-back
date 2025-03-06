package com.sg.bbit.wallet.dto;

import java.util.List;
import lombok.Data;

@Data
public class MultisigWalletDTO {
    private String address;
    private String redeemScript;
    private List<String> publicKeys;
    private List<String> privateKeys;
}