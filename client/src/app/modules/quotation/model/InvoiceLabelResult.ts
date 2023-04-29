import { City } from "../../miscellaneous/model/City";
import { Country } from "../../miscellaneous/model/Country";
import { BillingLabelType } from "../../tiers/model/BillingLabelType";

export interface InvoiceLabelResult {
  billingLabel: string;
  billingLabelAddress: string;
  billingLabelPostalCode: string;
  cedexComplement: string;
  billingLabelCity: City;
  billingLabelCountry: Country;
  billingLabelIsIndividual: boolean;
  billingLabelType: BillingLabelType;
  isResponsableOnBilling: boolean;
  isCommandNumberMandatory: boolean;
  commandNumber: string;
  labelOrigin: string;
}
