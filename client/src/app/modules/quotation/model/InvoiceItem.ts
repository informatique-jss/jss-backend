import { AccountingAccount } from "../../accounting/model/AccountingAccount";
import { BillingItem } from "../../miscellaneous/model/BillingItem";
import { Vat } from "../../miscellaneous/model/Vat";
import { Invoice } from "./Invoice";
import { IQuotation } from "./IQuotation";
import { Provision } from "./Provision";

export interface InvoiceItem {
  id: number;
  label: string;
  billingItem: BillingItem;
  accountingAccount: AccountingAccount;
  preTaxPrice: number;
  vatPrice: number;
  vat: Vat;
  discountAmount: number;
  invoice: Invoice;
  quotation: IQuotation;
  customerOrder: IQuotation;
  provision: Provision;
}
