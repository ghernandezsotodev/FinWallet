package com.example.wallet;

import com.example.wallet.model.Wallet;
import com.example.wallet.repository.WalletRepository;
import com.example.wallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class WalletConcurrencyTest {

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletRepository walletRepository;

    @Test
    void testConcurrentWithdrawals() throws InterruptedException {
        // Creamos una billetera y le depositamos $100.
        Wallet wallet = walletService.createWallet("");
        walletService.deposit(wallet.getId(), new BigDecimal("100.00"), "");

        int threadCount = 2;

        ExecutorService executorService =
                Executors.newFixedThreadPool(threadCount);

        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successfulWithdrawals = new AtomicInteger(0);

        // Los dos hilos intentan retirar los mismos $100 al mismo tiempo.
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    walletService.withdraw(
                            wallet.getId(),
                            new BigDecimal("100.00"),
                            ""
                    );

                    successfulWithdrawals.incrementAndGet();

                } catch (Exception e) {
                    System.out.println(
                            "Retiro rechazado: " + e.getMessage()
                    );
                } finally {
                    latch.countDown();
                }
            });
        }

        // Esperamos a que terminen ambos retiros.
        latch.await();
        executorService.shutdown();

        // Consultamos el saldo que quedó guardado en la base de datos.
        Wallet updatedWallet = walletRepository
                .findById(wallet.getId())
                .orElseThrow();

        // Solo uno de los dos retiros debería completarse.
        assertEquals(
                1,
                successfulWithdrawals.get(),
                "Solo un retiro debió procesarse con éxito"
        );

        assertEquals(
                0,
                updatedWallet.getBalance().compareTo(BigDecimal.ZERO),
                "El saldo final debe ser 0"
        );
    }
}