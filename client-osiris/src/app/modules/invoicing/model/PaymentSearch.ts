
export interface PaymentSearch {
    minAmount: number;
    maxAmount: number;
    labelContent: string;
    startDate: Date;
    endDate: Date;
    tiersId: number;
    responsableId: number;
}
