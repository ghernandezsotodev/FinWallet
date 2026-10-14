package com.example.wallet.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferRequest(Long originWalletId, Long destinationWalletId, @NotNull @Positive BigDecimal amount) {
}
