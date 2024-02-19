export interface CustomerOrderFiller {
  customerOrderId: number;
  createdDateCO: Date;
  affaireLabel: string;
  customerOrderStatus: string;
  quotationStatus: string;
  invoiceId: number;
  createdDateInvoice: Date;
  dateFacture: Date;
  totalPriceInvoice: number;
  remainingToPayInvoice: number;
  provisionStatus: string;
}
