
export interface PaymentSearchResult {
  id: number;
  paymentDate: Date;
  paymentAmount: number;
  paymentLabel: string;
  paymentTypeLabel: string;
  isExternallyAssociated: boolean;
  isAssociated: boolean;
  isCancelled: boolean;
  isAppoint: boolean;
  invoiceId: number;
  comment: string;
  originPaymentId: number;
}
