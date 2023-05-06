import { Invoice } from "../../quotation/model/Invoice";
import { Payment } from "./Payment";
import { Refund } from "./Refund";

export interface Appoint {
  id: number;
  label: string;
  originPayment: Payment;
  appointAmount: number;
  appointDate: Date;
  refunds: Refund[];
  invoice: Invoice;
}
