
export interface RefundSearchResult {
  id: number;
  refundDate: Date;
  refundAmount: number;
  refundLabel: string;
  refundTiersLabel: string;
  refundIban: string;
  isMatched: boolean;
  isAlreadyExported: boolean;
  paymentId: number;
  affaireLabel: string;
}
