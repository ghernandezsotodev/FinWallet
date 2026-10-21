import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  WalletResponse,
  DepositRequest,
  WithdrawRequest,
  TransferRequest
} from '../models/wallet.model';

@Injectable({
  providedIn: 'root'
})
export class WalletService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api/wallets';

  getMyWallet(): Observable<WalletResponse> {
    return this.http.get<WalletResponse>(this.apiUrl);
  }

  deposit(walletId: number, request: DepositRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/${walletId}/deposit`, request);
  }

  withdraw(walletId: number, request: WithdrawRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/${walletId}/withdraw`, request);
  }

  transfer(request: TransferRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/transfer`, request);
  }
}
