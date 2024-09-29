
export interface PaperSetResult {
  id: number;
  paperSetTypeLabel: string;
  customerOrderId: number;
  customerOrderStatus: string;
  tiersLabel: string;
  tiersId: string;
  responsableLabel: string;
  responsableId: string;
  affaireLabel: string;
  servicesLabel: string;
  locationNumber: number;
  isValidated: boolean;
  isCancelled: boolean;
  creationComment: string;
}
