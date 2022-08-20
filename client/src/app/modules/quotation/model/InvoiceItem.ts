import { BillingItem } from "../../miscellaneous/model/BillingItem";
import { Vat } from "../../miscellaneous/model/Vat";
import { Invoice } from "./Invoice";
import { Provision } from "./Provision";

export interface InvoiceItem {
  id: number;
  label: string;
  billingItem: BillingItem;
  preTaxPrice: number;
  vatPrice: number;
  vat: Vat;
  discountAmount: number;
  invoice: Invoice;
  provision: Provision;
}
