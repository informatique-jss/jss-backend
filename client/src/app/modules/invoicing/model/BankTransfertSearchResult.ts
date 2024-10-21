
export interface BankTransfertSearchResult {
  id: number;
  transfertDate: Date;
  transfertAmount: number;
  transfertLabel: string;
  transfertIban: string;
  isAlreadyExported: boolean;
  isSelectedForExport: boolean;
  isMatched: boolean;
  competentAuthorityLabel: string;
  invoiceBillingLabel: string;
  affaireLabel: string;
  comment: string;
  isMatched: boolean;
}
