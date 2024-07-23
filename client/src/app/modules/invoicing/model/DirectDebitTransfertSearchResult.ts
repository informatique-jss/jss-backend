
export interface DirectDebitTransfertSearchResult {
  id: number;
  customerOrderLabel: string;
  transfertDate: Date;
  transfertAmount: number;
  transfertLabel: string;
  transfertIban: string;
  isAlreadyExported: boolean;
  isMatched: boolean;
  competentAuthorityLabel: string;
  invoiceBillingLabel: string;
}
