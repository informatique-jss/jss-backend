import { AccountingAccount } from "../../accounting/model/AccountingAccount";
import { BillingItem } from "../../miscellaneous/model/BillingItem";
import { IQuotation } from "./IQuotation";
import { Provision } from "./Provision";

export interface InvoiceItem {
  id: number;
  label: string;
  billingItem: BillingItem;
  accountingAccount: AccountingAccount;
  preTaxPrice: number;
  vatPrice: number;
  discountAmount: number;
  quotation: IQuotation;
  customerOrder: IQuotation;
  provision: Provision;
}
