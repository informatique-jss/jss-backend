import { Payment } from "../../invoicing/model/Payment";
import { BillingType } from "../../miscellaneous/model/BillingType";
import { CompetentAuthority } from "../../miscellaneous/model/CompetentAuthority";
import { PaymentType } from "../../miscellaneous/model/PaymentType";
import { InvoiceItem } from './InvoiceItem';
import { Provision } from "./Provision";

export interface Debour {
  id: number;
  comments: string;
  provision: Provision;
  billingType: BillingType;
  competentAuthority: CompetentAuthority;
  debourAmount: number;
  invoicedAmount: number;
  nonTaxableAmount: number;
  paymentType: PaymentType;
  paymentDateTime: Date;
  payment: Payment;
  checkNumber: string;
  invoiceItem: InvoiceItem;
}
