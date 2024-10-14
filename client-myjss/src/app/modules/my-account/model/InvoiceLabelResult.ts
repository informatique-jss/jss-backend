import { City } from "../../profile/model/City";
import { Country } from "../../profile/model/Country";

export interface InvoiceLabelResult {
  billingLabel: string;
  billingLabelAddress: string;
  billingLabelPostalCode: string;
  cedexComplement: string;
  billingLabelCity: City;
  billingLabelCountry: Country;
  commandNumber: string;
}
