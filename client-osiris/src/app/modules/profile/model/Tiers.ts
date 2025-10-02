import { City } from "./City";
import { Country } from "./Country";
import { Mail } from "./Mail";
import { Phone } from "./Phone";
import { SpecialOffer } from "./SpecialOffer";


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
  specialOffers: SpecialOffer[];
  firstname: string;
  lastname: string;
  isIndividual: boolean;
  selected: boolean;
}
