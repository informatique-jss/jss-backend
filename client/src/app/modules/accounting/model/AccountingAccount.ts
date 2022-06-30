import { IReferential } from "../../administration/model/IReferential";
import { BillingItem } from "../../miscellaneous/model/BillingItem";
import { Vat } from "../../miscellaneous/model/Vat";
import { AccountingAccountClass } from "./AccountingAccountClass";

export interface AccountingAccount extends IReferential {
  accountingAccountNumber: string;
  vat: Vat;
  accountingAccountClass: AccountingAccountClass;
  billingItem: BillingItem;
}
