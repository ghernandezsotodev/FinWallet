import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { environment } from '../../../environments/environment';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);

    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('debería enviar la petición de login y almacenar el token', () => {
    const mockRequest = { email: 'test@example.com', password: 'password123' };
    const mockResponse = { token: 'jwt.token.simulado' };

    service.login(mockRequest).subscribe((response) => {
      expect(response.token).toBe(mockResponse.token);
      expect(service.getToken()).toBe(mockResponse.token);
      expect(service.isAuthenticated()).toBe(true);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);

    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockRequest);

    req.flush(mockResponse);
  });
});
