package com.example.wallet.service;

import com.example.wallet.model.Wallet;
import com.example.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Transactional
@Service
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet createWallet(Long userId){
        Wallet walletInstance = new Wallet(userId, BigDecimal.ZERO);
        return walletRepository.save(walletInstance);
    }

    public Wallet deposit(Long walletId, BigDecimal amount){
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new RuntimeException("Wallet not found"));
        wallet.setBalance(wallet.getBalance().add(amount));
        return walletRepository.save(wallet);
    }

    public Wallet withdraw(Long walletId, BigDecimal amount){
        Wallet wallet = walletRepository.findByIdForUpdate(walletId).orElseThrow(() -> new RuntimeException("Wallet not found"));
        if(wallet.getBalance().compareTo(amount) < 0){
            throw new RuntimeException("Insufficient funds");
        }
        wallet.setBalance(wallet.getBalance().subtract(amount));
        return walletRepository.save(wallet);
    }
}
