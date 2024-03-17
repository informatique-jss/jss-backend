export interface CustomerOrderFiller {
  customerOrderId: number;
  createdDateCO: Date | null;
  affaireLabel: string;
  customerOrderStatus: string;
  invoiceId: number;
  createdDateInvoice: Date | null;
  dateFacture: Date | null;
  totalPriceInvoice: number;
  remainingToPayInvoice: number;
  responsableLabel: string;
  depositTotalAmount: number;
  nbrCommandes: number;
}
