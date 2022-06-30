import { AccountingAccount } from "../../accounting/model/AccountingAccount";
import { AssoSpecialOfferBillingType } from "./AssoSpecialOfferBillingType";
import { BillingType } from "./BillingType";

export interface BillingItem {
  id: number;
  billingType: BillingType;
  preTaxPrice: number;
  startDate: Date;
  assoSpecialOfferBillingItem: AssoSpecialOfferBillingType[];
  accountingAccounts: AccountingAccount[];
}
