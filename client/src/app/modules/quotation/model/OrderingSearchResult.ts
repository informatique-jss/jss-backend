
export interface OrderingSearchResult {
  tiersLabel: string;
  customerOrderLabel: string;
  customerOrderStatus: string;
  createdDate: Date;
  productionEffectiveDate: Date;
  salesEmployeeId: number;
  customerOrderDescription: string;
  customerOrderId: number;
  responsableId: number;
  tiersId: number;
  affaireLabel: string;
  totalPrice: number;
  depositTotalAmount: number;
  quotationId: number;
  affaireSiren: string;
  affaireAddress: string;
  lastStatusUpdate: Date;
  customerOrderOriginLabel: string;
  serviceTypeLabel: string;

  // Recurring
  customerOrderParentRecurringId: number;
  recurringPeriodStartDate: Date;
  recurringPeriodEndDate: Date;
  recurringStartDate: Date;
  recurringEndDate: Date;
  isRecurringAutomaticallyBilled: boolean;
}


