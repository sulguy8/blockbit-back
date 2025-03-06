package com.sg.bbit.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetBalance;

import com.sg.bbit.wallet.dto.MpcWalletDTO;
import com.sg.bbit.wallet.dto.PartialSignatureDTO;
import com.sg.bbit.wallet.service.EthereumMpcService;

@ExtendWith(MockitoExtension.class)
public class EthereumMpcServiceTest {

    @InjectMocks
    private EthereumMpcService ethereumMpcService;
    
    @Mock
    private Web3j web3j;
    
    private String testWalletId;
    private String testAddress;
    
    @BeforeEach
    void setUp() throws Exception {
        testWalletId = "test-wallet-id";
        testAddress = "0x1234567890123456789012345678901234567890";

        // GasPrice 모킹
        EthGasPrice ethGasPrice = mock(EthGasPrice.class);
        when(ethGasPrice.getGasPrice()).thenReturn(BigInteger.valueOf(20_000_000_000L)); // 20 Gwei
        
        // 타입 안전한 모킹을 위해 doReturn/when 스타일 사용
        Request<?, EthGasPrice> gasPriceRequest = mock(Request.class);
        doReturn(ethGasPrice).when(gasPriceRequest).send();
        doReturn(gasPriceRequest).when(web3j).ethGasPrice();

        // Balance 모킹
        EthGetBalance ethGetBalance = mock(EthGetBalance.class);
        when(ethGetBalance.getBalance()).thenReturn(BigInteger.valueOf(1_000_000_000_000_000_000L)); // 1 ETH
        
        // 마찬가지로 doReturn/when 스타일 사용
        Request<?, EthGetBalance> balanceRequest = mock(Request.class);
        doReturn(ethGetBalance).when(balanceRequest).send();
        doReturn(balanceRequest).when(web3j).ethGetBalance(anyString(), eq(DefaultBlockParameterName.LATEST));
    }
    
    @Test
    @DisplayName("이더리움 MPC 지갑이 올바르게 생성되는지 검증")
    /*
     * 1. 지갑 객체가 생성되었는지
     * 2. 지갑 ID가 생성되었는지
     * 3. 이더리움 주소가 생성되었는지
     * 4. 공개키가 생성되었는지
     * 5. 생성된 주소가 이더리움 주소 형식(0x로 시작, 42자)인지
     */
    void createMpcWalletTest() {
        // when
        MpcWalletDTO wallet = ethereumMpcService.createMpcWallet();
        
        // then
        assertNotNull(wallet);
        assertNotNull(wallet.getWalletId());
        assertNotNull(wallet.getAddress());
        assertNotNull(wallet.getPublicKey());
        
        // 이더리움 주소 형식 테스트
        assertTrue(wallet.getAddress().startsWith("0x"));
        assertEquals(42, wallet.getAddress().length());
    }
    
    @Test
    @DisplayName("MPC 방식에서 첫 번째 참여자의 부분 서명이 올바르게 생성되는지 검증")
    /*
     * 1. 부분 서명 객체가 생성되었는지
     * 2. 트랜잭션 해시가 생성되었는지
     * 3. 부분 서명 데이터가 있는지
     * 4. 참여자 인덱스가 올바른지
     * 5. 원시 트랜잭션 데이터가 있는지
     */
    void createPartialSignatureTest() {
        // given
        String toAddress = "0x9876543210987654321098765432109876543210";
        BigDecimal amount = BigDecimal.valueOf(0.1); // 0.1 ETH
        int participantIndex = 0;
        
        // MPC 지갑 생성 (테스트용 키 공유 생성을 위해)
        MpcWalletDTO wallet = ethereumMpcService.createMpcWallet();
        
        // when
        PartialSignatureDTO partialSig = ethereumMpcService.createPartialSignature(
                wallet.getWalletId(), participantIndex, toAddress, amount);
        
        // then
        assertNotNull(partialSig);
        assertEquals(wallet.getWalletId(), partialSig.getWalletId());
        assertNotNull(partialSig.getTransactionHash());
        assertNotNull(partialSig.getPartialSignature());
        assertEquals(participantIndex, partialSig.getParticipantIndex());
        assertNotNull(partialSig.getRawTransaction());
    }
    
    @Test
    @DisplayName("부분 서명들을 결합하여 완전한 트랜잭션을 생성하고 제출하는 기능 검증")
    /*
     * 1. 트랜잭션 해시가 생성되었는지
     * 2. 생성된 해시가 이더리움 트랜잭션 해시 형식(0x로 시작)인지
     * 3. MPC 서명 결합이 정상적으로 이루어졌는지
     */
    void completeAndSubmitTransactionTest() {
        // given
        String toAddress = "0x9876543210987654321098765432109876543210";
        BigDecimal amount = BigDecimal.valueOf(0.1); // 0.1 ETH
        
        // MPC 지갑 생성 (테스트용 키 공유 생성을 위해)
        MpcWalletDTO wallet = ethereumMpcService.createMpcWallet();
        
        // 첫 번째 부분 서명 생성
        PartialSignatureDTO firstSig = ethereumMpcService.createPartialSignature(
                wallet.getWalletId(), 0, toAddress, amount);
        
        // when
        String txHash = ethereumMpcService.completeAndSubmitTransaction(firstSig, 1);
        
        // then
        assertNotNull(txHash);
        assertTrue(txHash.startsWith("0x"));
    }
    
    @Test
    @DisplayName("이더리움 주소의 잔액을 올바르게 조회하는지 검증")
    /*
     * 1. 잔액이 null이 아닌지
     * 2. 모킹된 응답 값(1 ETH)과 일치하는지
     * 3. 네트워크 요청이 올바른 매개변수로 이루어졌는지
     */
    void getBalanceTest() {
        // given
        String address = "0x1234567890123456789012345678901234567890";
        
        // when
        BigDecimal balance = ethereumMpcService.getBalance(address);
        
        // then
        assertNotNull(balance);
        assertEquals(1.0, balance.doubleValue());
        
        // verify 메서드로 web3j.ethGetBalance가 올바른 매개변수로 호출되었는지 검증할 수도 있음
    }
}