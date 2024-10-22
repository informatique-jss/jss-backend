export interface OutboundCheckSearchResult {
  outboundCheckNumber: number;
  paymentNumber: number;
  outboundCheckDate: Date;
  outboundCheckAmount: number;
  outboundCheckLabel: string;
  invoiceAssociated: number;
  isMatched: boolean;
}
