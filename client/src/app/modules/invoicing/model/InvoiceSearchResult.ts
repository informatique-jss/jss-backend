
export interface InvoiceSearchResult {
  invoiceId: number;
  invoiceStatus: string;
  invoiceStatusCode: string;
  invoiceStatusId: number;
  customerOrderId: number;
  customerOrderLabel: string;
  providerLabel: string;
  tiersLabel: string;
  confrereId: number;
  responsableId: number;
  responsableCommercialId: number;
  tiersId: number;
  responsableLabel: string;
  affaireLabel: string;
  billingLabel: string;
  createdDate: Date;
  firstReminderDateTime: Date;
  secondReminderDateTime: Date;
  thirdReminderDateTime: Date;
  manualAccountingDocumentNumber: string;
  manualAccountingDocumentDate: Date;
  dueDate: Date;
  lastFollowupDate: Date;
  totalPrice: number;
  customerOrderDescription: string;
  paymentId: string;
  remainingToPay: number;
  idPaymentType: number;
  invoiceRecipient: string;
  isInvoiceFromProvider: boolean;
  isProviderCreditNote: boolean;
}


