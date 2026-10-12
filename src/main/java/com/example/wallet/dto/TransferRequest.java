package com.example.wallet.dto;

import java.math.BigDecimal;

public record TransferRequest(Long originWalletId, Long destinationWalletId, BigDecimal amount) {
}
