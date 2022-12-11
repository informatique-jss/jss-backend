
export interface RefundSearchResult {
  id: number;
  refundDate: Date;
  refundAmount: number;
  refundLabel: string;
  refundIban: string;
  isMatched: boolean;
  isAlreadyExported: boolean;
  paymentId: number;
}
