
export interface BankTransfertSearchResult {
  id: number;
  transfertDate: Date;
  transfertAmount: number;
  transfertLabel: string;
  transfertIban: string;
  isAlreadyExported: boolean;
  isSelectedForExport: boolean;
  invoiceBillingLabel: string;
  affaireLabel: string;
  comment: string;
}
