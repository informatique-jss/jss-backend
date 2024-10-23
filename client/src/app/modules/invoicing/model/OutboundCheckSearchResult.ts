export interface OutboundCheckSearchResult {
  outboundCheckNumber: string;
  paymentNumber: number;
  outboundCheckDate: Date;
  outboundCheckAmount: number;
  outboundCheckLabel: string;
  invoiceAssociated: number;
  isMatched: boolean;
}
