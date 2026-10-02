package com.example.wallet.controller;

import com.example.wallet.dto.DepositRequest;
import com.example.wallet.dto.WalletResponse;
import com.example.wallet.model.Wallet;
import com.example.wallet.service.WalletService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public WalletResponse createWallet(@RequestParam Long userId) {
        Wallet wallet = walletService.createWallet(userId);
        return new WalletResponse(
                wallet.getId(),
                wallet.getUserId(),
                wallet.getBalance()
        );
    }

    @PostMapping("/{id}/deposit")
    public WalletResponse deposit(@PathVariable Long id, @RequestBody DepositRequest request){
        Wallet wallet = walletService.deposit(id, request.amount());
        return new WalletResponse(
                wallet.getId(),
                wallet.getUserId(),
                wallet.getBalance()
        );
    }
}
