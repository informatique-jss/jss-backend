
export interface DebourSearchResult {
  id: number;
  comments: string;
  customerOrderId: number;
  billingTypeLabel: string;
  competentAuthorityLabel: string;
  debourAmount: number;
  paymentTypeLabel: string;
  paymentDateTime: Date;
  paymentId: number;
  invoiceId: number;
  checkNumber: string;
  isAssociated: boolean;
  isCompetentAuthorityDirectCharge: boolean;
}
