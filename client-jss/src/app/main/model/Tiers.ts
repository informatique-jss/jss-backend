import { City } from "./City";
import { Country } from "./Country";
import { Mail } from "./Mail";
import { Phone } from "./Phone";


export interface Tiers {
  id: number;
  denomination: string | null;
  address: string;
  postalCode: string;
  cedexComplement: string;
  city: City;
  country: Country;
  mails: Mail[];
  phones: Phone[];
  firstname: string;
  lastname: string;
  isIndividual: boolean;
}
