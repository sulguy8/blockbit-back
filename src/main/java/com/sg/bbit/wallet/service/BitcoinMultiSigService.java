package com.sg.bbit.wallet.service;

import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutPoint;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.springframework.stereotype.Service;

import com.sg.bbit.wallet.dto.MultisigWalletDTO;
import com.sg.bbit.wallet.dto.PartiallySignedTransactionDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BitcoinMultiSigService {

    private final NetworkParameters params = NetworkParameters.fromID(NetworkParameters.ID_TESTNET);

    /**
     * 2-of-3 멀티시그 지갑 생성
     */
    public MultisigWalletDTO createMultisigWallet() {
        try {
            // 3개의 키 쌍 생성
            List<ECKey> keys = new ArrayList<>();
            List<String> publicKeys = new ArrayList<>();
            List<String> privateKeys = new ArrayList<>();
            
            for (int i = 0; i < 3; i++) {
                ECKey key = new ECKey();
                keys.add(key);
                publicKeys.add(key.getPublicKeyAsHex());
                privateKeys.add(key.getPrivateKeyAsHex());
            }
            
            // 2-of-3 멀티시그 스크립트 생성
            Script redeemScript = ScriptBuilder.createMultiSigOutputScript(2, keys);
            
            // P2SH 주소 생성
            Address multiSigAddress = ScriptBuilder.createP2SHOutputScript(redeemScript).getToAddress(params);
            
            // 결과 반환
            MultisigWalletDTO result = new MultisigWalletDTO();
            result.setAddress(multiSigAddress.toString());
            result.setRedeemScript(Utils.HEX.encode(redeemScript.getProgram()));
            result.setPublicKeys(publicKeys);
            result.setPrivateKeys(privateKeys); // 실제로는 서버에 저장하지 말고 안전하게 분배
            
            return result;
        } catch (Exception e) {
            log.error("멀티시그 지갑 생성 오류", e);
            throw new RuntimeException("멀티시그 지갑 생성 실패", e);
        }
    }
    
    /**
     * 멀티시그 트랜잭션 생성 (서명 1개 포함)
     */
    public PartiallySignedTransactionDTO createMultisigTransaction(
            String fromAddress, 
            String toAddress, 
            long amountSatoshi, 
            String redeemScriptHex, 
            String privateKeyHex) {
        
        try {
            // 이전 트랜잭션 정보 (실제로는 UTXO 조회 필요)
            // 예시 목적으로 하드코딩 - 실제 구현에서는 API로 UTXO 조회
            String prevTxId = "000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f";
            int prevOutputIndex = 0;
            long prevOutputValue = amountSatoshi + 1000; // 수수료 포함
            
            // 리딤 스크립트 파싱
            Script redeemScript = new Script(Utils.HEX.decode(redeemScriptHex));
            
            // 개인키 로드
            ECKey privateKey = ECKey.fromPrivate(Utils.HEX.decode(privateKeyHex));
            
            // 트랜잭션 생성
            Transaction tx = new Transaction(params);
            
            // 입력 추가 - 수정된 부분
            TransactionOutPoint outPoint = new TransactionOutPoint(params, prevOutputIndex, Sha256Hash.wrap(prevTxId));
            TransactionInput input = new TransactionInput(params, tx, new byte[]{}, outPoint, Coin.valueOf(prevOutputValue));
            tx.addInput(input);
            
            // 출력 추가 (받는 주소로)
            tx.addOutput(Coin.valueOf(amountSatoshi), Address.fromString(params, toAddress));
            
            // 서명 (1개)
            TransactionInput txInput = tx.getInput(0);
            Sha256Hash sigHash = tx.hashForSignature(0, redeemScript, Transaction.SigHash.ALL, false);
            ECKey.ECDSASignature signature = privateKey.sign(sigHash);
            TransactionSignature txSig = new TransactionSignature(signature, Transaction.SigHash.ALL, false);
            
            // 부분적으로 서명된 트랜잭션 반환
            PartiallySignedTransactionDTO result = new PartiallySignedTransactionDTO();
            result.setTransactionHex(Utils.HEX.encode(tx.bitcoinSerialize()));
            result.setSignatureHex(Utils.HEX.encode(txSig.encodeToBitcoin()));
            result.setPublicKeyHex(privateKey.getPublicKeyAsHex());
            result.setRedeemScriptHex(redeemScriptHex);
            
            return result;
        } catch (Exception e) {
            log.error("트랜잭션 생성 오류", e);
            throw new RuntimeException("트랜잭션 생성 실패", e);
        }
    }
    
    /**
     * 멀티시그 트랜잭션 서명 추가 (두 번째 서명)
     */
    public String addSignatureToTransaction(
            PartiallySignedTransactionDTO partialTx, 
            String privateKeyHex) {
        
        try {
            // 트랜잭션 복원
            byte[] txBytes = Utils.HEX.decode(partialTx.getTransactionHex());
            Transaction tx = new Transaction(params, txBytes);
            
            // 개인키 로드
            ECKey privateKey = ECKey.fromPrivate(Utils.HEX.decode(privateKeyHex));
            
            // 리딤 스크립트 복원
            Script redeemScript = new Script(Utils.HEX.decode(partialTx.getRedeemScriptHex()));
            
            // 두 번째 서명 생성
            TransactionInput input = tx.getInput(0);
            Sha256Hash sigHash = tx.hashForSignature(0, redeemScript, Transaction.SigHash.ALL, false);
            ECKey.ECDSASignature sig2 = privateKey.sign(sigHash);
            TransactionSignature txSig2 = new TransactionSignature(sig2, Transaction.SigHash.ALL, false);
            
            // 첫 번째 서명 복원
            byte[] sig1Bytes = Utils.HEX.decode(partialTx.getSignatureHex());
            TransactionSignature txSig1 = TransactionSignature.decodeFromBitcoin(sig1Bytes, false, true); // requireCanonical 추가
            
            // 첫 번째 서명자의 공개키
            byte[] pubKey1Bytes = Utils.HEX.decode(partialTx.getPublicKeyHex());
            ECKey pubKey1 = ECKey.fromPublicOnly(pubKey1Bytes);
            
            // 서명 스크립트 생성 (OP_0 <sig1> <sig2> <redeemScript>)
            Script inputScript = ScriptBuilder.createP2SHMultiSigInputScript(
                    List.of(txSig1, txSig2),
                    redeemScript
            );
            
            // 트랜잭션에 서명 스크립트 설정
            input.setScriptSig(inputScript);
            
            // 완성된 트랜잭션 직렬화
            return Utils.HEX.encode(tx.bitcoinSerialize());
        } catch (Exception e) {
            log.error("트랜잭션 서명 추가 오류", e);
            throw new RuntimeException("트랜잭션 서명 추가 실패", e);
        }
    }
}