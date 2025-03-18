export interface OutboundCheckSearch {
  startDate: Date | undefined;
  endDate: Date | undefined;
  minAmount: number;
  maxAmount: number;
  label: string;
  isDisplayNonMatchedOutboundChecks: boolean;
}
