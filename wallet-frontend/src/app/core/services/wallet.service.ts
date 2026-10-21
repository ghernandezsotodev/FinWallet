import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { WalletResponse } from '../models/wallet.model';

@Injectable({
  providedIn: 'root'
})
export class WalletService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/wallets`;

  getMyWallet(): Observable<WalletResponse> {
    return this.http.get<WalletResponse>(this.apiUrl);
  }
}
