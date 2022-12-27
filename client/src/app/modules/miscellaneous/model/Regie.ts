import { IReferential } from "../../administration/model/IReferential";
import { City } from "./City";
import { Country } from "./Country";
import { Mail } from "./Mail";
import { Phone } from "./Phone";

export interface Regie extends IReferential {
  country: Country;
  city: City;
  postalCode: string;
  cedexComplement: string;
  address: string;
  phones: Phone[];
  mails: Mail[];
  iban: string;
  bic: string;
}

