
export interface PaymentSearchResult {
  id: number;
  paymentWayLabel: string;
  paymentWayId: number;
  paymentDate: Date;
  paymentAmount: number;
  paymentLabel: string;
  isExternallyAssociated: boolean;
  isCancelled: boolean;
  invoiceId: number;
}
