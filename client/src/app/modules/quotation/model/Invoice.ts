import { City } from "../../miscellaneous/model/City";
import { Country } from "../../miscellaneous/model/Country";
import { BillingLabelType } from "../../tiers/model/BillingLabelType";
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
}
