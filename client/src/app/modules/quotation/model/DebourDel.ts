import { BillingType } from "../../miscellaneous/model/BillingType";
import { CompetentAuthority } from "../../miscellaneous/model/CompetentAuthority";
import { PaymentType } from "../../miscellaneous/model/PaymentType";
import { Provision } from "./Provision";

export interface DebourDel {
  id: number;
  comments: string;
  checkNumber: string;
  debourAmount: number;
  invoicedAmount: number;
  paymentDateTime: Date;
  billingType: BillingType;
  competentAuthority: CompetentAuthority;
  paymentType: PaymentType;
  provision: Provision;
}
