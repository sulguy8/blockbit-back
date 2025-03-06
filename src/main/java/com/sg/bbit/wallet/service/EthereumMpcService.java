package com.sg.bbit.wallet.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import com.google.common.primitives.Bytes;
import com.sg.bbit.wallet.dto.MpcWalletDTO;
import com.sg.bbit.wallet.dto.PartialSignatureDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EthereumMpcService {

    private final Web3j web3j;
    private final String infuraUrl = "https://goerli.infura.io/v3/YOUR_INFURA_KEY"; // 테스트넷 사용
    
    // 키 공유 저장소 (실제로는 보다 안전한 저장소 사용)
    private final Map<String, KeyShare> keyShares = new ConcurrentHashMap<>();
    
    public EthereumMpcService() {
        this.web3j = Web3j.build(new HttpService(infuraUrl));
    }
    
    /**
     * MPC를 통한 이더리움 지갑 생성
     * 이 예제에서는 2-of-3 MPC 구현 시뮬레이션
     */
    public MpcWalletDTO createMpcWallet() {
        try {
            // MPC 키 생성 세션 ID
            String sessionId = UUID.randomUUID().toString();
            
            // 각 참여자의 키 공유 생성 (실제 MPC에서는 참여자 간 안전한 통신 필요)
            KeyShare[] shares = new KeyShare[3];
            
            // 실제 MPC 대신 데모 목적으로 ECKeyPair 생성
            ECKeyPair baseKeyPair = Keys.createEcKeyPair();
            String privateKeyHex = baseKeyPair.getPrivateKey().toString(16);
            String publicKeyHex = baseKeyPair.getPublicKey().toString(16);
            
            // 시뮬레이션을 위한 키 분할 (실제로는 MPC 프로토콜 구현)
            for (int i = 0; i < 3; i++) {
                KeyShare share = new KeyShare();
                share.id = sessionId;
                share.index = i;
                share.privateKeyShare = privateKeyHex + "_share_" + i; // 실제로는 안전한 비밀 분산 방식 사용
                
                shares[i] = share;
                keyShares.put(sessionId + "_" + i, share);
            }
            
            // 이더리움 주소 계산
            String address = computeAddressFromPublicKey(publicKeyHex);
            
            // DB에 지갑 정보 저장 (실제 구현에서 추가)
            
            // 결과 생성
            MpcWalletDTO result = new MpcWalletDTO();
            result.setWalletId(sessionId);
            result.setAddress(address);
            result.setPublicKey(publicKeyHex);
            
            return result;
        } catch (Exception e) {
            log.error("MPC 지갑 생성 오류", e);
            throw new RuntimeException("MPC 지갑 생성 실패", e);
        }
    }
    
    /**
     * MPC를 통한 트랜잭션 서명
     * 이 예제에서는 2-of-3 MPC 구현의 첫 번째 서명 단계 시뮬레이션
     */
    public PartialSignatureDTO createPartialSignature(
            String walletId, 
            int participantIndex,
            String toAddress, 
            BigDecimal etherAmount) {
        
        try {
            // 키 공유 가져오기
            KeyShare keyShare = keyShares.get(walletId + "_" + participantIndex);
            if (keyShare == null) {
                throw new RuntimeException("키 공유를 찾을 수 없습니다");
            }
            
            // 지갑 정보 가져오기 (실제로는 DB에서 조회)
            String walletAddress = "";  // 실제 구현에서는 DB 또는 저장소에서 조회
            
            // 트랜잭션 데이터 준비
            BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
            BigInteger gasLimit = BigInteger.valueOf(21000); // 기본 전송
            
            BigInteger nonce = getNonce(walletAddress);
            BigInteger value = Convert.toWei(etherAmount, Convert.Unit.ETHER).toBigInteger();
            
            RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                    nonce, gasPrice, gasLimit, toAddress, value);
            
            byte[] encodedTransaction = TransactionEncoder.encode(rawTransaction);
            String transactionHash = Numeric.toHexString(Hash.sha3(encodedTransaction));
            
            // 부분 서명 생성 시뮬레이션 (실제로는 MPC 프로토콜 구현)
            String partialSignature = "partial_signature_" + keyShare.privateKeyShare + "_" + transactionHash;
            
            // 결과 생성
            PartialSignatureDTO result = new PartialSignatureDTO();
            result.setWalletId(walletId);
            result.setTransactionHash(transactionHash);
            result.setPartialSignature(partialSignature);
            result.setParticipantIndex(participantIndex);
            result.setRawTransaction(Numeric.toHexString(encodedTransaction));
            
            return result;
        } catch (Exception e) {
            log.error("부분 서명 생성 오류", e);
            throw new RuntimeException("부분 서명 생성 실패", e);
        }
    }
    
    /**
     * MPC 트랜잭션 완료 (두 번째 서명 추가) 시뮬레이션
     */
    public String completeAndSubmitTransaction(
            PartialSignatureDTO firstSignature,
            int secondParticipantIndex) {
        
        try {
            String walletId = firstSignature.getWalletId();
            
            // 두 번째 키 공유 가져오기
            KeyShare secondKeyShare = keyShares.get(walletId + "_" + secondParticipantIndex);
            if (secondKeyShare == null) {
                throw new RuntimeException("두 번째 키 공유를 찾을 수 없습니다");
            }
            
            // 두 번째 부분 서명 생성 시뮬레이션
            String secondPartialSignature = "partial_signature_" + secondKeyShare.privateKeyShare + "_" + firstSignature.getTransactionHash();
            
            // 서명 결합 시뮬레이션 (실제로는 MPC 프로토콜에 따른 서명 결합)
            String[] partialSignatures = new String[] {
                firstSignature.getPartialSignature(),
                secondPartialSignature
            };
            
            // 데모 목적으로 임의의 서명 생성 (실제로는 부분 서명을 결합)
            byte[] r = new byte[32];
            byte[] s = new byte[32];
            byte v = 0x1b;
            new SecureRandom().nextBytes(r);
            new SecureRandom().nextBytes(s);
            byte[] signature = new byte[65];
            System.arraycopy(r, 0, signature, 0, 32);
            System.arraycopy(s, 0, signature, 32, 32);
            signature[64] = v;
            
            // 서명된 트랜잭션 생성
            byte[] rawTransactionBytes = Numeric.hexStringToByteArray(firstSignature.getRawTransaction());
            byte[] signedTransaction = Bytes.concat(rawTransactionBytes, signature);
            
            // 트랜잭션 전송 (실제로는 서명된 트랜잭션 전송)
            // 데모 목적으로 전송 생략, 임의의 트랜잭션 해시 반환
            return "0x" + Numeric.toHexString(Hash.sha3(signedTransaction)).substring(2);
        } catch (Exception e) {
            log.error("트랜잭션 완료 오류", e);
            throw new RuntimeException("트랜잭션 완료 실패", e);
        }
    }
    
    /**
     * 지갑 잔액 조회
     */
    public BigDecimal getBalance(String address) {
        try {
            EthGetBalance ethGetBalance = web3j
                    .ethGetBalance(address, DefaultBlockParameterName.LATEST)
                    .send();
            
            BigInteger wei = ethGetBalance.getBalance();
            return Convert.fromWei(new BigDecimal(wei), Convert.Unit.ETHER);
        } catch (Exception e) {
            log.error("잔액 조회 오류", e);
            throw new RuntimeException("잔액 조회 실패", e);
        }
    }
    
    /**
     * 공개키로부터 이더리움 주소 계산
     */
    private String computeAddressFromPublicKey(String publicKey) {
        String cleanPublicKey = publicKey.startsWith("0x") ? publicKey.substring(2) : publicKey;
        byte[] publicKeyBytes = Numeric.hexStringToByteArray(cleanPublicKey);
        byte[] addressBytes = Hash.sha3(publicKeyBytes);
        return "0x" + Numeric.toHexString(addressBytes).substring(24);
    }
    
    /**
     * 계정의 현재 nonce 조회
     */
    private BigInteger getNonce(String address) throws IOException {
        EthGetTransactionCount ethGetTransactionCount = web3j
                .ethGetTransactionCount(address, DefaultBlockParameterName.LATEST)
                .send();
        return ethGetTransactionCount.getTransactionCount();
    }
    
    // MPC 키 공유 클래스 (실제 구현에서는 MPC 라이브러리의 클래스 사용)
    private static class KeyShare {
        private String id;
        private int index;
        private String privateKeyShare;
    }
}