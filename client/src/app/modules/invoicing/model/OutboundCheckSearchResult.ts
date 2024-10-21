export interface OutboundCheckSearchResult {
  outboundCheckNumber: number;
  outboundCheckDate: Date;
  outboundCheckAmount: number;
  outboundCheckLabel: string;
  isMatched: boolean;
}
