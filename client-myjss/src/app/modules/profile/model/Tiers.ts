import { Mail } from "../../general/model/Mail";
import { City } from "./City";
import { Country } from "./Country";
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
}
