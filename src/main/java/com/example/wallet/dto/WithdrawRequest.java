package com.example.wallet.dto;

import java.math.BigDecimal;

public record WithdrawRequest(BigDecimal amount) {
}
