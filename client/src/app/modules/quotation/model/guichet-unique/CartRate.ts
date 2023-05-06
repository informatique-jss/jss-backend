import { Rate } from "./Rate";

export interface CartRate {
  rate: Rate;
  quantity: number;
  amount: number;
  isForcedManually: boolean;
  htAmount: number;
  recipientCode?: string;
  recipientName: string;
  subTotal: number;
}
