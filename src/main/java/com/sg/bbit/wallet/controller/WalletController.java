package com.sg.bbit.wallet.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sg.bbit.wallet.dto.BitcoinCompleteRequestDTO;
import com.sg.bbit.wallet.dto.BitcoinTransactionRequestDTO;
import com.sg.bbit.wallet.dto.EthereumCompleteRequestDTO;
import com.sg.bbit.wallet.dto.EthereumTransactionRequestDTO;
import com.sg.bbit.wallet.dto.MpcWalletDTO;
import com.sg.bbit.wallet.dto.MultisigWalletDTO;
import com.sg.bbit.wallet.dto.PartialSignatureDTO;
import com.sg.bbit.wallet.dto.PartiallySignedTransactionDTO;
import com.sg.bbit.wallet.service.BitcoinMultiSigService;
import com.sg.bbit.wallet.service.EthereumMpcService;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final BitcoinMultiSigService bitcoinMultiSigService;
    private final EthereumMpcService ethereumMpcService;
    
    @Autowired
    public WalletController(
            BitcoinMultiSigService bitcoinMultiSigService,
            EthereumMpcService ethereumMpcService) {
        this.bitcoinMultiSigService = bitcoinMultiSigService;
        this.ethereumMpcService = ethereumMpcService;
    }
    
    // 비트코인 멀티시그 지갑 생성
    @PostMapping("/bitcoin/create")
    public ResponseEntity<MultisigWalletDTO> createBitcoinWallet() {
        MultisigWalletDTO wallet = bitcoinMultiSigService.createMultisigWallet();
        return ResponseEntity.ok(wallet);
    }
    
    // 비트코인 멀티시그 트랜잭션 생성 (첫 번째 서명)
    @PostMapping("/bitcoin/transaction/create")
    public ResponseEntity<PartiallySignedTransactionDTO> createBitcoinTransaction(
            @RequestBody BitcoinTransactionRequestDTO request) {
        
        PartiallySignedTransactionDTO tx = bitcoinMultiSigService.createMultisigTransaction(
                request.getFromAddress(),
                request.getToAddress(),
                request.getAmountSatoshi(),
                request.getRedeemScriptHex(),
                request.getPrivateKeyHex()
        );
        
        return ResponseEntity.ok(tx);
    }
    
    // 비트코인 멀티시그 트랜잭션 완료 (두 번째 서명)
    @PostMapping("/bitcoin/transaction/complete")
    public ResponseEntity<String> completeBitcoinTransaction(
            @RequestBody BitcoinCompleteRequestDTO request) {
        
        String signedTx = bitcoinMultiSigService.addSignatureToTransaction(
                request.getPartiallySignedTransaction(),
                request.getPrivateKeyHex()
        );
        
        return ResponseEntity.ok(signedTx);
    }
    
    // 이더리움 MPC 지갑 생성
    @PostMapping("/ethereum/create")
    public ResponseEntity<MpcWalletDTO> createEthereumWallet() {
        MpcWalletDTO wallet = ethereumMpcService.createMpcWallet();
        return ResponseEntity.ok(wallet);
    }
    
    // 이더리움 MPC 트랜잭션 생성 (첫 번째 서명)
    @PostMapping("/ethereum/transaction/create")
    public ResponseEntity<PartialSignatureDTO> createEthereumTransaction(
            @RequestBody EthereumTransactionRequestDTO request) {
        
        PartialSignatureDTO partialSig = ethereumMpcService.createPartialSignature(
                request.getWalletId(),
                request.getParticipantIndex(),
                request.getToAddress(),
                request.getAmount()
        );
        
        return ResponseEntity.ok(partialSig);
    }
    
    // 이더리움 MPC 트랜잭션 완료 (두 번째 서명)
    @PostMapping("/ethereum/transaction/complete")
    public ResponseEntity<String> completeEthereumTransaction(
            @RequestBody EthereumCompleteRequestDTO request) {
        
        String txHash = ethereumMpcService.completeAndSubmitTransaction(
                request.getFirstSignature(),
                request.getSecondParticipantIndex()
        );
        
        return ResponseEntity.ok(txHash);
    }
    
    // 이더리움 잔액 조회
    @GetMapping("/ethereum/balance/{address}")
    public ResponseEntity<BigDecimal> getEthereumBalance(@PathVariable String address) {
        BigDecimal balance = ethereumMpcService.getBalance(address);
        return ResponseEntity.ok(balance);
    }
}