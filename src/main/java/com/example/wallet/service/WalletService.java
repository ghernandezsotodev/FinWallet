package com.example.wallet.service;

import com.example.wallet.model.User;
import com.example.wallet.model.Wallet;
import com.example.wallet.repository.UserRepository;
import com.example.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Transactional
@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    public WalletService(WalletRepository walletRepository, UserRepository userRepository) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
    }

    public Wallet createWallet(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));

        Wallet walletInstance = new Wallet(user.getId(), BigDecimal.ZERO);

        return walletRepository.save(walletInstance);
    }

    public Wallet deposit(Long walletId, BigDecimal amount, String email){
        User user  = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("WalletService not found"));

        if(!wallet.getUserId().equals(user.getId())){
            throw  new RuntimeException("Access denied: You do not own this wallet.");
        }

        wallet.setBalance(wallet.getBalance().add(amount));

        return walletRepository.save(wallet);
    }

    public Wallet withdraw(Long walletId, BigDecimal amount, String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));

        Wallet wallet = walletRepository.findByIdForUpdate(walletId)
                .orElseThrow(() -> new RuntimeException("WalletService not found"));

        if(!wallet.getUserId().equals(user.getId())){
            throw new RuntimeException("Access denied: You do not own this wallet.");
        }

        if(wallet.getBalance().compareTo(amount) < 0){
            throw new RuntimeException("Insufficient funds");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        return walletRepository.save(wallet);
    }

    public void transfer(Long originWalletId, Long destinationWalletId, BigDecimal amount, String email){
        this.withdraw(originWalletId, amount, email);

        Wallet destinationWallet = walletRepository.findByIdForUpdate(destinationWalletId)
                .orElseThrow(() -> new RuntimeException("Destination wallet not found."));

        destinationWallet.setBalance(destinationWallet.getBalance().add(amount));
        walletRepository.save(destinationWallet);

    }

    public Wallet getWalletByUserEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));

        return walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("WalletService not found for this user."));
    }
}
