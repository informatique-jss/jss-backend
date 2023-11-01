
export interface PaymentSearch {
  startDate: Date | undefined;
  endDate: Date | undefined;
  minAmount: number;
  maxAmount: number;
  label: string;
  isHideAssociatedPayments: boolean;
  isHideCancelledPayments: boolean;
}
