
export interface PaymentSearchResult {
  id: number;
  paymentWayLabel: string;
  paymentWayId: number;
  paymentDate: Date;
  paymentAmount: number;
  paymentLabel: string;
  isExternallyAssociated: boolean;
  invoiceId: number;
  customerOrderId: number;
}
