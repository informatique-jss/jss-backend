import { AccountingRecord } from "../../accounting/model/AccountingRecord";
import { Deposit } from "../../invoicing/model/Deposit";
import { InvoiceStatus } from "../../invoicing/model/InvoiceStatus";
import { Payment } from "../../invoicing/model/Payment";
import { City } from "../../miscellaneous/model/City";
import { Country } from "../../miscellaneous/model/Country";
import { BillingLabelType } from "../../tiers/model/BillingLabelType";
import { CustomerOrder } from "./CustomerOrder";
import { InvoiceItem } from "./InvoiceItem";

export interface Invoice {
  id: number;
  createdDate: Date;
  dueDate: Date;
  invoiceItems: InvoiceItem[];
  billingLabel: string;
  billingLabelAddress: string;
  billingLabelPostalCode: string;
  billingLabelCity: City;
  billingLabelCountry: Country;
  billingLabelIsIndividual: boolean;
  billingLabelType: BillingLabelType;
  isResponsableOnBilling: boolean;
  isCommandNumberMandatory: boolean;
  commandNumber: string;
  customerOrder: CustomerOrder;
  totalPrice: number;
  payments: Payment[];
  deposits: Deposit[];
  invoiceStatus: InvoiceStatus;
  accountingRecords: AccountingRecord[];
}
