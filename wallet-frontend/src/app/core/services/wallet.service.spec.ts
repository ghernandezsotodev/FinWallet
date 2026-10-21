import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { WalletService } from './wallet.service';
import { environment } from '../../../environments/environment';
import { WalletResponse } from '../models/wallet.model';

describe('WalletService', () => {
  let service: WalletService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        WalletService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(WalletService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('debería obtener la billetera del usuario', () => {
    const mockWallet: WalletResponse = { id: 9, userId: 3, balance: 0.00 };

    service.getMyWallet().subscribe((response) => {
      expect(response).toEqual(mockWallet);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/wallets`);

    expect(req.request.method).toBe('GET');

    req.flush(mockWallet);
  });
});
