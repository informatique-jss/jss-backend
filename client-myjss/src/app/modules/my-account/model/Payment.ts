import { PaymentType } from "./PaymentType";
import { Refund } from "./Refund";

export interface Payment {
  id: number;
  paymentDate: Date;
  paymentAmount: number;
  label: string;
  paymentType: PaymentType;
  refund: Refund;
}
