
export interface PaymentSearchResult {
  id: number;
  paymentWayLabel: string;
  paymentDate: Date;
  paymentAmount: number;
  paymentLabel: string;
  isExternallyAssociated: boolean;
  invoiceId: number;
  customerOrderId: number;
}
