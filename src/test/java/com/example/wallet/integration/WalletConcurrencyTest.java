package com.example.wallet.integration;

import com.example.wallet.model.User;
import com.example.wallet.model.Wallet;
import com.example.wallet.repository.UserRepository;
import com.example.wallet.repository.WalletRepository;
import com.example.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
class WalletConcurrencyTest {

    @Container
    @SuppressWarnings("resource")
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("test_wallet_db")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    private Long testWalletId;
    private final String testEmail = "concurrent@test.com";

    @BeforeEach
    void setUp() {
        walletRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setEmail(testEmail);
        user.setPassword("hashed");
        userRepository.save(user);

        Wallet wallet = new Wallet();
        wallet.setUserId(user.getId());
        wallet.setBalance(BigDecimal.valueOf(100));
        testWalletId = walletRepository.save(wallet).getId();
    }

    @Test
    void testConcurrentWithdrawals() throws InterruptedException {
        int numberOfThreads = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        BigDecimal withdrawAmount = BigDecimal.valueOf(50);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(() -> {
                try {
                    walletService.withdraw(testWalletId, withdrawAmount, testEmail);
                    System.out.println("Retiro exitoso de " + withdrawAmount);
                } catch (Exception e) {
                    System.out.println("Retiro rechazado: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        Wallet updatedWallet = walletRepository.findById(testWalletId).orElseThrow();

        assertEquals(0, updatedWallet.getBalance().compareTo(BigDecimal.ZERO),
                "El saldo final debe ser 0 sin permitir saldos negativos por concurrencia");
    }
}