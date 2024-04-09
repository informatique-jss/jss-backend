import { BillingType } from "../../miscellaneous/model/BillingType";
import { Domiciliation } from "./Domiciliation";

export interface DomiciliationFee {
  id: number;
  billingType: BillingType;
  amount: number;
  feeDate: Date;
  domiciliation: Domiciliation;
}
