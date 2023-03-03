
export interface BankTransfertSearchResult {
  id: number;
  transfertDate: Date;
  transfertAmount: number;
  transfertLabel: string;
  transfertIban: string;
  isAlreadyExported: boolean;
  competentAuthorityLabel: string;
  invoiceBillingLabel: string;
}
