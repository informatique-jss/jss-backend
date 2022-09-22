import { PaymentWay } from "./PaymentWay";

export interface PaymentSearch {
  startDate: Date | undefined;
  endDate: Date | undefined;
  paymentWays: PaymentWay[];
}
