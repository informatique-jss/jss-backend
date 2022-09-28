import { PaymentWay } from "./PaymentWay";

export interface PaymentSearch {
  startDate: Date | undefined;
  endDate: Date | undefined;
  minAmount: number;
  maxAmount: number;
  label: string;
  paymentWays: PaymentWay[];
}
