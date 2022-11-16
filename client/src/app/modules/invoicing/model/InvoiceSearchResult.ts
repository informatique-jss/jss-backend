
export interface InvoiceSearchResult {
  invoiceId: number;
  invoiceStatus: string;
  customerOrderId: number;
  customerOrderLabel: string;
  confrereId: number;
  responsableId: number;
  tiersId: number;
  responsableLabel: string;
  affaireLabel: string;
  billingLabel: string;
  createdDate: Date;
  totalPrice: number;
  customerOrderDescription: string;
  paymentId: string;
}

