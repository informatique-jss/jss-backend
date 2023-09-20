
export interface BankTransfertSearchResult {
  id: number;
  transfertDate: Date;
  transfertAmount: number;
  transfertLabel: string;
  transfertIban: string;
  isAlreadyExported: boolean;
  isSelectedForExport: boolean;
  competentAuthorityLabel: string;
  invoiceBillingLabel: string;
  affaireLabel: string;
}
