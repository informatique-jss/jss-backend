
export interface PaymentSearchResult {
  id: number;
  paymentWayLabel: string;
  paymentWayId: number;
  paymentDate: Date;
  paymentAmount: number;
  paymentLabel: string;
  paymentTypeLabel: string;
  isExternallyAssociated: boolean;
  isAssociated: boolean;
  isCancelled: boolean;
  invoiceId: number;
}
