package com.example.wallet.controller;

import com.example.wallet.dto.DepositRequest;
import com.example.wallet.dto.TransferRequest;
import com.example.wallet.dto.WalletResponse;
import com.example.wallet.dto.WithdrawRequest;
import com.example.wallet.model.Wallet;
import com.example.wallet.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public WalletResponse createWallet(Principal principal) {
        Wallet wallet = walletService.createWallet(principal.getName());
        return new WalletResponse(
                wallet.getId(),
                wallet.getUserId(),
                wallet.getBalance()
        );
    }

    @PostMapping("/{id}/deposit")
    public WalletResponse deposit(@PathVariable Long id, @Valid @RequestBody DepositRequest request, Principal principal){
        Wallet wallet = walletService.deposit(id, request.amount(), principal.getName());
        return new WalletResponse(
                wallet.getId(),
                wallet.getUserId(),
                wallet.getBalance()
        );
    }

    @PostMapping("/{id}/withdraw")
    public WalletResponse withdraw(@PathVariable Long id, @Valid @RequestBody WithdrawRequest request, Principal principal){
        Wallet wallet = walletService.withdraw(id, request.amount(), principal.getName());
        return new WalletResponse(
                wallet.getId(),
                wallet.getUserId(),
                wallet.getBalance()
        );
    }

    @PostMapping("/transfer")
    public void transfer(@Valid @RequestBody TransferRequest transferRequest, Principal principal){
        walletService.transfer(
                transferRequest.originWalletId(),
                transferRequest.destinationWalletId(),
                transferRequest.amount(),
                principal.getName()
        );
    }

    @GetMapping
    public WalletResponse getMyWallet(Principal principal) {
        Wallet wallet = walletService.getWalletByUserEmail(principal.getName());

        return new WalletResponse(
                wallet.getId(),
                wallet.getUserId(),
                wallet.getBalance()
        );
    }
}
