import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  loginForm = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]]
  });

  errorMessage = signal<string>('');
  isLoading = signal<boolean>(false);

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set('');

    this.authService.login(this.loginForm.getRawValue()).subscribe({
      next: () => {
        console.log('¡Login exitoso! Token guardado.');
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.isLoading.set(false);

        if (err.status === 401 || err.status === 403) {
          this.errorMessage.set('Correo o contraseña incorrectos.');
        } else {
          this.errorMessage.set('Error de conexión. Inténtalo más tarde.');
        }
        console.error('Login error:', err);
      }
    });
  }
}
