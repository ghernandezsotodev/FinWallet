package com.example.wallet.service;

import com.example.wallet.model.User;
import com.example.wallet.model.Wallet;
import com.example.wallet.repository.UserRepository;
import com.example.wallet.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WalletService walletService;

    @Test
    void deposit_Success(){
        String email = "test@test.com";
        Long walletId = 1L;
        User mockUser = new User(1L, email, "hashedpwd");
        Wallet mockWallet = new Wallet(1L, BigDecimal.valueOf(100));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(mockWallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Wallet updatedWallet  = walletService.deposit(walletId, BigDecimal.valueOf(50), email);

        assertEquals(BigDecimal.valueOf(150) , updatedWallet.getBalance());
        verify(walletRepository, times(1)).save(mockWallet);

    }

    @Test
    void deposit_AccessDenied_ThrowsException(){
        String hackerEmail = "hacker@test.com";
        Long victimWalletId = 1L;

        User mockHacker = new User(2L, hackerEmail, "hashedpwd");

        Wallet victimWallet = new Wallet(1L, BigDecimal.valueOf(100));

        when(userRepository.findByEmail(hackerEmail)).thenReturn(Optional.of(mockHacker));
        when(walletRepository.findById(victimWalletId)).thenReturn(Optional.of(victimWallet));

        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> walletService.deposit(victimWalletId, BigDecimal.valueOf(50),hackerEmail)
        );

        assertEquals("Access denied: You do not own this wallet.", exception.getMessage());

        verify(walletRepository, never()).save(any(Wallet.class));
    }


}
