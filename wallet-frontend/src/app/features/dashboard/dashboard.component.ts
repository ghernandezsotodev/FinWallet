import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { WalletService } from '../../core/services/wallet.service';
import { AuthService } from '../../core/services/auth.service';
import { WalletResponse } from '../../core/models/wallet.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, CurrencyPipe, ReactiveFormsModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  private readonly walletService = inject(WalletService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);

  wallet = signal<WalletResponse | null>(null);
  isLoading = signal<boolean>(true);
  errorMessage = signal<string>('');

  activeModal = signal<'deposit' | 'withdraw' | 'transfer' | null>(null);
  isOperating = signal<boolean>(false);
  operationMessage = signal<{type: 'success' | 'error', text: string} | null>(null);

  depositForm = this.fb.nonNullable.group({
    amount: [0, [Validators.required, Validators.min(0.01)]]
  });

  withdrawForm = this.fb.nonNullable.group({
    amount: [0, [Validators.required, Validators.min(0.01)]]
  });

  transferForm = this.fb.nonNullable.group({
    destinationWalletId: [0, [Validators.required, Validators.min(1)]],
    amount: [0, [Validators.required, Validators.min(0.01)]]
  });

  ngOnInit(): void {
    this.loadWallet();
  }

  loadWallet(): void {
    this.isLoading.set(true);
    this.errorMessage.set('');

    this.walletService.getMyWallet().subscribe({
      next: (data) => {
        this.wallet.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Error fetching wallet:', err);
        this.errorMessage.set('No se pudo cargar la información de la billetera.');
        this.isLoading.set(false);
      }
    });
  }

  openModal(type: 'deposit' | 'withdraw' | 'transfer'): void {
    this.operationMessage.set(null);
    this.depositForm.reset({ amount: 0 });
    this.withdrawForm.reset({ amount: 0 });
    this.transferForm.reset({ destinationWalletId: 0, amount: 0 });
    this.activeModal.set(type);
  }

  closeModal(): void {
    this.activeModal.set(null);
  }

  onDeposit(): void {
    if (this.depositForm.invalid || !this.wallet()) return;
    this.executeOperation(
      this.walletService.deposit(this.wallet()!.id, this.depositForm.getRawValue()),
      'Depósito realizado con éxito'
    );
  }

  onWithdraw(): void {
    if (this.withdrawForm.invalid || !this.wallet()) return;
    this.executeOperation(
      this.walletService.withdraw(this.wallet()!.id, this.withdrawForm.getRawValue()),
      'Retiro realizado con éxito'
    );
  }

  onTransfer(): void {
    if (this.transferForm.invalid || !this.wallet()) return;
    const formValue = this.transferForm.getRawValue();

    if (formValue.destinationWalletId === this.wallet()!.id) {
      this.operationMessage.set({
        type: 'error',
        text: 'No puedes realizar una transferencia a tu propia billetera.'
      });
      return;
    }

    this.executeOperation(
      this.walletService.transfer({
        originWalletId: this.wallet()!.id,
        destinationWalletId: formValue.destinationWalletId,
        amount: formValue.amount
      }),
      'Transferencia realizada con éxito'
    );
  }

  private executeOperation(operation$: any, successMsg: string): void {
    this.isOperating.set(true);
    this.operationMessage.set(null);

    operation$.subscribe({
      next: () => {
        this.operationMessage.set({ type: 'success', text: successMsg });
        this.loadWallet();
        setTimeout(() => this.closeModal(), 2000);
      },
      error: (err: any) => {
        console.error('Operation error:', err);
        this.operationMessage.set({
          type: 'error',
          text: 'La operación falló. Verifica tus fondos o el ID de destino.'
        });
        this.isOperating.set(false);
      },
      complete: () => this.isOperating.set(false)
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
