package com.sg.bbit.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sg.bbit.wallet.controller.WalletController;
import com.sg.bbit.wallet.dto.BitcoinTransactionRequestDTO;
import com.sg.bbit.wallet.dto.EthereumTransactionRequestDTO;
import com.sg.bbit.wallet.dto.MpcWalletDTO;
import com.sg.bbit.wallet.dto.MultisigWalletDTO;
import com.sg.bbit.wallet.dto.PartialSignatureDTO;
import com.sg.bbit.wallet.dto.PartiallySignedTransactionDTO;
import com.sg.bbit.wallet.service.BitcoinMultiSigService;
import com.sg.bbit.wallet.service.EthereumMpcService;

@WebMvcTest(WalletController.class)
public class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private BitcoinMultiSigService bitcoinMultiSigService;
    
    @MockBean
    private EthereumMpcService ethereumMpcService;
    
    private MultisigWalletDTO mockBitcoinWallet;
    private MpcWalletDTO mockEthereumWallet;
    private PartiallySignedTransactionDTO mockPartialBtcTx;
    private PartialSignatureDTO mockPartialEthSig;
    
    @BeforeEach
    void setUp() {
        // 비트코인 지갑 목업
        mockBitcoinWallet = new MultisigWalletDTO();
        mockBitcoinWallet.setAddress("2N8hwP1WmJrFF5QWABn38y63uYLhnJYJYTF");
        mockBitcoinWallet.setRedeemScript("mock_redeem_script");
        
        // 이더리움 지갑 목업
        mockEthereumWallet = new MpcWalletDTO();
        mockEthereumWallet.setWalletId("mock-wallet-id");
        mockEthereumWallet.setAddress("0x1234567890123456789012345678901234567890");
        mockEthereumWallet.setPublicKey("mock_public_key");
        
        // 부분 서명된 비트코인 트랜잭션 목업
        mockPartialBtcTx = new PartiallySignedTransactionDTO();
        mockPartialBtcTx.setTransactionHex("mock_tx_hex");
        mockPartialBtcTx.setSignatureHex("mock_sig_hex");
        mockPartialBtcTx.setPublicKeyHex("mock_pub_key_hex");
        mockPartialBtcTx.setRedeemScriptHex("mock_redeem_script_hex");
        
        // 부분 서명된 이더리움 트랜잭션 목업
        mockPartialEthSig = new PartialSignatureDTO();
        mockPartialEthSig.setWalletId("mock-wallet-id");
        mockPartialEthSig.setTransactionHash("0xmock_tx_hash");
        mockPartialEthSig.setPartialSignature("mock_partial_sig");
        mockPartialEthSig.setParticipantIndex(0);
        mockPartialEthSig.setRawTransaction("0xmock_raw_tx");
    }
    
    @Test
    @DisplayName("비트코인 지갑 생성 API 테스트")
    void createBitcoinWalletTest() throws Exception {
        when(bitcoinMultiSigService.createMultisigWallet()).thenReturn(mockBitcoinWallet);
        
        mockMvc.perform(post("/api/wallet/bitcoin/create")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value(mockBitcoinWallet.getAddress()));
    }
    
    @Test
    @DisplayName("비트코인 트랜잭션 생성 API 테스트")
    void createBitcoinTransactionTest() throws Exception {
        // given
        BitcoinTransactionRequestDTO request = new BitcoinTransactionRequestDTO();
        request.setFromAddress("2N8hwP1WmJrFF5QWABn38y63uYLhnJYJYTF");
        request.setToAddress("mwCwTceJvYV27KXBc3NJZys6CjsgsoeHmf");
        request.setAmountSatoshi(10000L);
        request.setRedeemScriptHex("mock_redeem_script_hex");
        request.setPrivateKeyHex("mock_private_key_hex");
        
        when(bitcoinMultiSigService.createMultisigTransaction(
                anyString(), anyString(), anyLong(), anyString(), anyString()))
                .thenReturn(mockPartialBtcTx);
        
        // when & then
        mockMvc.perform(post("/api/wallet/bitcoin/transaction/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionHex").value(mockPartialBtcTx.getTransactionHex()));
    }
    
    @Test
    @DisplayName("이더리움 지갑 생성 API 테스트")
    void createEthereumWalletTest() throws Exception {
        when(ethereumMpcService.createMpcWallet()).thenReturn(mockEthereumWallet);
        
        mockMvc.perform(post("/api/wallet/ethereum/create")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletId").value(mockEthereumWallet.getWalletId()))
                .andExpect(jsonPath("$.address").value(mockEthereumWallet.getAddress()));
    }
    
    @Test
    @DisplayName("이더리움 트랜잭션 생성 API 테스트")
    void createEthereumTransactionTest() throws Exception {
        // given
        EthereumTransactionRequestDTO request = new EthereumTransactionRequestDTO();
        request.setWalletId("mock-wallet-id");
        request.setParticipantIndex(0);
        request.setToAddress("0x9876543210987654321098765432109876543210");
        request.setAmount(BigDecimal.valueOf(0.1));
        
        when(ethereumMpcService.createPartialSignature(
                anyString(), anyInt(), anyString(), any(BigDecimal.class)))
                .thenReturn(mockPartialEthSig);
        
        // when & then
        mockMvc.perform(post("/api/wallet/ethereum/transaction/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletId").value(mockPartialEthSig.getWalletId()))
                .andExpect(jsonPath("$.transactionHash").value(mockPartialEthSig.getTransactionHash()));
    }
    
    @Test
    @DisplayName("이더리움 잔액 조회 API 테스트")
    void getEthereumBalanceTest() throws Exception {
        // given
        String address = "0x1234567890123456789012345678901234567890";
        BigDecimal expectedBalance = BigDecimal.valueOf(1.5);
        
        when(ethereumMpcService.getBalance(address)).thenReturn(expectedBalance);
        
        // when & then
        mockMvc.perform(get("/api/wallet/ethereum/balance/{address}", address)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedBalance.toString()));
    }
}