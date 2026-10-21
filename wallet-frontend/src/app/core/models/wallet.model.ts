export interface WalletResponse {
  id: number;
  userId: number;
  balance: number;
}

export interface DepositRequest {
  amount: number;
}

export interface WithdrawRequest {
  amount: number;
}

export interface TransferRequest {
  originWalletId: number;
  destinationWalletId: number;
  amount: number;
}
