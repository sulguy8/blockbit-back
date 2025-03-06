package com.sg.bbit.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.sg.bbit.wallet.dto.BitcoinCompleteRequestDTO;
import com.sg.bbit.wallet.dto.BitcoinTransactionRequestDTO;
import com.sg.bbit.wallet.dto.EthereumCompleteRequestDTO;
import com.sg.bbit.wallet.dto.EthereumTransactionRequestDTO;
import com.sg.bbit.wallet.dto.MpcWalletDTO;
import com.sg.bbit.wallet.dto.MultisigWalletDTO;
import com.sg.bbit.wallet.dto.PartialSignatureDTO;
import com.sg.bbit.wallet.dto.PartiallySignedTransactionDTO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WalletIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;
    
    // 테스트용 변수들
    private static MultisigWalletDTO bitcoinWallet;
    private static MpcWalletDTO ethereumWallet;
    private static PartiallySignedTransactionDTO partialBtcTx;
    private static PartialSignatureDTO partialEthSig;
    private static String completedBtcTx;
    private static String completedEthTxHash;
    
    @Test
    @Order(1)
    @DisplayName("REST API를 통해 비트코인 멀티시그 지갑이 정상적으로 생성되는지 검증")
    /*
     * 1. API 응답 상태 코드가 200 OK
     * 2. 응답 바디가 존재하는지
     * 3. 지갑 주소가 생성되었는지
     * 4. 리딤 스크립트가 생성되었는지
     */
    void createBitcoinWalletIntegrationTest() {
        // when
        ResponseEntity<MultisigWalletDTO> response = restTemplate.postForEntity(
                "/api/wallet/bitcoin/create", null, MultisigWalletDTO.class);
        
        // then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        
        bitcoinWallet = response.getBody();
        assertNotNull(bitcoinWallet.getAddress());
        assertNotNull(bitcoinWallet.getRedeemScript());
    }
    
    @Test
    @Order(2)
    @DisplayName("REST API를 통해 이더리움 MPC 지갑이 정상적으로 생성되는지 검증")
    /*
     * 1. API 응답 상태 코드가 200 OK
     * 2. 응답 바디가 존재하는지
     * 3. 지갑 ID가 생성되었는지
     * 4. 이더리움 주소가 생성되었는지
     */
    void createEthereumWalletIntegrationTest() {
        // when
        ResponseEntity<MpcWalletDTO> response = restTemplate.postForEntity(
                "/api/wallet/ethereum/create", null, MpcWalletDTO.class);
        
        // then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        
        ethereumWallet = response.getBody();
        assertNotNull(ethereumWallet.getWalletId());
        assertNotNull(ethereumWallet.getAddress());
    }
    
    @Test
    @Order(3)
    @DisplayName("REST API를 통해 비트코인 멀티시그 지갑의 첫 번째 서명이 정상적으로 생성되는지 검증")
    /*
     * 1. 이전 테스트에서 지갑이 정상적으로 생성되었는지 확인
     * 2. API 응답 상태 코드가 200 OK인지
     * 3. 부분 서명된 트랜잭션이 생성되었는지
     * 4. 트랜잭션 데이터와 서명이 포함되었는지
     */
    void createBitcoinTransactionIntegrationTest() {
        // given (1번 테스트에서 지갑 생성 필요)
        assertNotNull(bitcoinWallet, "비트코인 지갑이 먼저 생성되어야 합니다");
        
        BitcoinTransactionRequestDTO request = new BitcoinTransactionRequestDTO();
        request.setFromAddress(bitcoinWallet.getAddress());
        request.setToAddress("mwCwTceJvYV27KXBc3NJZys6CjsgsoeHmf"); // 테스트넷 주소
        request.setAmountSatoshi(10000L);
        request.setRedeemScriptHex(bitcoinWallet.getRedeemScript());
        request.setPrivateKeyHex(bitcoinWallet.getPrivateKeys().get(0)); // 첫 번째 키로 서명
        
        // when
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<BitcoinTransactionRequestDTO> requestEntity = new HttpEntity<>(request, headers);
        
        ResponseEntity<PartiallySignedTransactionDTO> response = restTemplate.exchange(
                "/api/wallet/bitcoin/transaction/create",
                HttpMethod.POST,
                requestEntity,
                PartiallySignedTransactionDTO.class);
        
        // then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        
        partialBtcTx = response.getBody();
        assertNotNull(partialBtcTx.getTransactionHex());
        assertNotNull(partialBtcTx.getSignatureHex());
    }
    
    @Test
    @Order(4)
    @DisplayName("REST API를 통해 이더리움 MPC 지갑의 첫 번째 부분 서명이 정상적으로 생성되는지 검증")
    /*
     * 1. 이전 테스트에서 지갑이 정상적으로 생성되었는지 확인
     * 2. API 응답 상태 코드가 200 OK인지
     * 3. 부분 서명 정보가 생성되었는지
     * 4. 트랜잭션 해시와 부분 서명 데이터가 포함되었는지
     */
    void createEthereumTransactionIntegrationTest() {
        // given (2번 테스트에서 지갑 생성 필요)
        assertNotNull(ethereumWallet, "이더리움 지갑이 먼저 생성되어야 합니다");
        
        EthereumTransactionRequestDTO request = new EthereumTransactionRequestDTO();
        request.setWalletId(ethereumWallet.getWalletId());
        request.setParticipantIndex(0); // 첫 번째 참여자
        request.setToAddress("0x9876543210987654321098765432109876543210");
        request.setAmount(BigDecimal.valueOf(0.01)); // 0.01 ETH
        
        // when
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<EthereumTransactionRequestDTO> requestEntity = new HttpEntity<>(request, headers);
        
        ResponseEntity<PartialSignatureDTO> response = restTemplate.exchange(
                "/api/wallet/ethereum/transaction/create",
                HttpMethod.POST,
                requestEntity,
                PartialSignatureDTO.class);
        
        // then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        
        partialEthSig = response.getBody();
        assertNotNull(partialEthSig.getTransactionHash());
        assertNotNull(partialEthSig.getPartialSignature());
    }
    
    @Test
    @Order(5)
    @DisplayName("REST API를 통해 비트코인 멀티시그 트랜잭션의 두 번째 서명 추가 및 완성이 정상적으로 이루어지는지 검증")
    /*
     * 1. 이전 테스트들이 정상적으로 완료되었는지 확인
     * 2. API 응답 상태 코드가 200 OK인지
     * 3. 완성된 트랜잭션 문자열이 반환되었는지
     * 4. 트랜잭션 문자열이 유효한지(빈 문자열이 아닌지)
     */
    void completeBitcoinTransactionIntegrationTest() {
        // given (3번 테스트에서 부분 서명 트랜잭션 생성 필요)
        assertNotNull(partialBtcTx, "부분 서명된 비트코인 트랜잭션이 먼저 생성되어야 합니다");
        assertNotNull(bitcoinWallet, "비트코인 지갑이 먼저 생성되어야 합니다");
        
        BitcoinCompleteRequestDTO request = new BitcoinCompleteRequestDTO();
        request.setPartiallySignedTransaction(partialBtcTx);
        request.setPrivateKeyHex(bitcoinWallet.getPrivateKeys().get(1)); // 두 번째 키로 서명
        
        // when
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<BitcoinCompleteRequestDTO> requestEntity = new HttpEntity<>(request, headers);
        
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/wallet/bitcoin/transaction/complete",
                HttpMethod.POST,
                requestEntity,
                String.class);
        
        // then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        
        completedBtcTx = response.getBody();
        assertTrue(completedBtcTx.length() > 0);
    }
    
    @Test
    @Order(6)
    @DisplayName("REST API를 통해 이더리움 MPC 트랜잭션의 두 번째 서명 추가 및 완성이 정상적으로 이루어지는지 검증")
    /*
     * 1. 이전 테스트에서 부분 서명이 정상적으로 생성되었는지 확인
     * 2. API 응답 상태 코드가 200 OK인지
     * 3. 트랜잭션 해시가 반환되었는지
     * 4. 반환된 해시가 이더리움 트랜잭션 해시 형식(0x로 시작)인지
     */
    void completeEthereumTransactionIntegrationTest() {
        // given (4번 테스트에서 부분 서명 생성 필요)
        assertNotNull(partialEthSig, "부분 서명된 이더리움 트랜잭션이 먼저 생성되어야 합니다");
        
        EthereumCompleteRequestDTO request = new EthereumCompleteRequestDTO();
        request.setFirstSignature(partialEthSig);
        request.setSecondParticipantIndex(1); // 두 번째 참여자
        
        // when
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<EthereumCompleteRequestDTO> requestEntity = new HttpEntity<>(request, headers);
        
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/wallet/ethereum/transaction/complete",
                HttpMethod.POST,
                requestEntity,
                String.class);
        
        // then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        
        completedEthTxHash = response.getBody();
        assertTrue(completedEthTxHash.startsWith("0x"));
    }
    
    @Test
    @Order(7)
    @DisplayName("REST API를 통해 이더리움 지갑의 잔액이 정상적으로 조회되는지 검증")
    /*
     * 1. 이전 테스트에서 지갑이 정상적으로 생성되었는지 확인
     * 2. API 응답 상태 코드가 200 OK인지
     * 3. 잔액 정보가 반환되었는지
     * 4. 잔액이 0 이상의 값인지
     */
    void getEthereumBalanceIntegrationTest() {
        // given
        assertNotNull(ethereumWallet, "이더리움 지갑이 먼저 생성되어야 합니다");
        String address = ethereumWallet.getAddress();
        
        // when
        ResponseEntity<BigDecimal> response = restTemplate.getForEntity(
                "/api/wallet/ethereum/balance/{address}",
                BigDecimal.class,
                address);
        
        // then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().compareTo(BigDecimal.ZERO) >= 0);
    }
}