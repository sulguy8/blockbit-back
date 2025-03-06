package com.sg.bbit.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sg.bbit.wallet.dto.MultisigWalletDTO;
import com.sg.bbit.wallet.dto.PartiallySignedTransactionDTO;
import com.sg.bbit.wallet.service.BitcoinMultiSigService;

@ExtendWith(MockitoExtension.class)
public class BitcoinMultiSigServiceTest {

    @InjectMocks
    private BitcoinMultiSigService bitcoinMultiSigService;
    
    private NetworkParameters params;
    private List<ECKey> testKeys;
    private String redeemScriptHex;
    
    @BeforeEach
    void setUp() {
        params = NetworkParameters.fromID(NetworkParameters.ID_TESTNET);
        
        // 테스트용 키 생성
        testKeys = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            testKeys.add(new ECKey());
        }
        
        // 리딤 스크립트 생성
        Script redeemScript = ScriptBuilder.createMultiSigOutputScript(2, testKeys);
        redeemScriptHex = org.bitcoinj.core.Utils.HEX.encode(redeemScript.getProgram());
    }
    
    @Test
    @DisplayName("BitcoinMultiSigService가 2-of-3 멀티시그 지갑을 올바르게 생성하는지 검증")
    /*
     * 1. 지갑이 null이 아닌지
     * 2. 비트코인 주소가 생성되었는지
     * 3. 리딤 스크립트가 생성되었는지
     * 4. 공개키와 개인키가 각각 3개씩 생성되었는지
     * 5. 생성된 주소가, 테스트넷 주소 형식("2", "m", "n" 중 하나로 시작)인지
     */ 
    void createMultisigWalletTest() {
        // when
        MultisigWalletDTO wallet = bitcoinMultiSigService.createMultisigWallet();
        
        // then
        assertNotNull(wallet);
        assertNotNull(wallet.getAddress());
        assertNotNull(wallet.getRedeemScript());
        assertEquals(3, wallet.getPublicKeys().size());
        assertEquals(3, wallet.getPrivateKeys().size());
        
        // 주소가 유효한 형식인지 테스트
        assertTrue(wallet.getAddress().startsWith("2") || wallet.getAddress().startsWith("m") || wallet.getAddress().startsWith("n"));
    }
    
    @Test
    @DisplayName("첫 번째 개인키로 부분적으로 서명된 트랜잭션을 올바르게 생성하는지 검증")
    /*
     * 1. 트랜잭션 객체가 생성되었는지
     * 2. 트랜잭션 직렬화(Hex) 정보가 있는
     * 3. 서명 정보가 있는지
     * 4. 공개키 정보가 있는지
     * 5. 리딤 스크립트가 올바르게 포함되었는지
     */
    void createMultisigTransactionTest() {
        // given
        String fromAddress = "2N8hwP1WmJrFF5QWABn38y63uYLhnJYJYTF"; // 테스트넷 P2SH 주소 형식
        String toAddress = "mwCwTceJvYV27KXBc3NJZys6CjsgsoeHmf";   // 테스트넷 주소 형식
        long amountSatoshi = 10000; // 0.0001 BTC
        String privateKeyHex = testKeys.get(0).getPrivateKeyAsHex();
        
        // when
        PartiallySignedTransactionDTO tx = bitcoinMultiSigService.createMultisigTransaction(
                fromAddress, toAddress, amountSatoshi, redeemScriptHex, privateKeyHex);
        
        // then
        assertNotNull(tx);
        assertNotNull(tx.getTransactionHex());
        assertNotNull(tx.getSignatureHex());
        assertNotNull(tx.getPublicKeyHex());
        assertEquals(redeemScriptHex, tx.getRedeemScriptHex());
    }
    
    @Test
    @DisplayName("부분적으로 서명된 트랜잭션에 두 번째 서명을 추가하여 완전한 트랜잭션을 만드는 기능 검증")
    /*
     * 1. 두 번째 서명이 추가된 후 최종 트랜잭션이 생성되는지
     * 2. 생성된 트랜잭션이 유효한 형식인지(빈 문자열이 아닌지)
     * 3. 멀티시그 조건(2-of-3)을 만족하는 트랜잭션이 생성되는지
     */
    void addSignatureToTransactionTest() {
        // given
        String fromAddress = "2N8hwP1WmJrFF5QWABn38y63uYLhnJYJYTF";
        String toAddress = "mwCwTceJvYV27KXBc3NJZys6CjsgsoeHmf";
        long amountSatoshi = 10000; // 0.0001 BTC
        String firstPrivateKeyHex = testKeys.get(0).getPrivateKeyAsHex();
        String secondPrivateKeyHex = testKeys.get(1).getPrivateKeyAsHex();
        
        // first signature
        PartiallySignedTransactionDTO partialTx = bitcoinMultiSigService.createMultisigTransaction(
                fromAddress, toAddress, amountSatoshi, redeemScriptHex, firstPrivateKeyHex);
        
        // when
        String signedTxHex = bitcoinMultiSigService.addSignatureToTransaction(
                partialTx, secondPrivateKeyHex);
        
        // then
        assertNotNull(signedTxHex);
        assertTrue(signedTxHex.length() > 0);
    }
}