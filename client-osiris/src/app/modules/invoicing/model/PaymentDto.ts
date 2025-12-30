
export interface PaymentDto {
    id: number;
    originPayment: number;
    paymentDate: Date;
    paymentAmount: number;
    paymentType: string;
    label: string[];
    isAssociated: boolean;
    isCancelled: boolean;
    isAppoint: boolean;
    invoiceId: number;
    comment: string;
}
