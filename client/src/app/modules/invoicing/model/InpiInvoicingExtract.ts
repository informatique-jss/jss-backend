export interface InpiInvoicingExtract {
  id: number;
  idInpiInvoicing: string;
  creationDate: Date;
  paymentReference: number;
  inpiReference: number;
  effectiveDate: Date;
  preTaxAmount: number;
  taxAmount: number;
  totalAmount: number;
  type: string;
  requestReference: string;
  companyName: string;
  clientReference: string;
}
