import { City } from "../../profile/model/City";
import { Country } from "../../profile/model/Country";
import { Mail } from "../../profile/model/Mail";

export interface TiersDto {
  id: number;
  denomination: string;
  lastname: string;
  firstname: string;
  address: string;
  postalCode: string;
  cedexComplement: string;
  city: City;
  country: Country;
  salesEmployee: string;
  formalisteEmployee: string;
  kpiValues: Record<string, string>;
  tiersCategory: string;
  tiersType: string;
  isNewTiers: boolean;
  siret: string;
  phones: string[];
  mails: Mail[];
  mailRecipient: string;
  rffFormaliteRate: number;
  rffInsertionRate: number;
  specialOffers: string[];
  instructions: string;
  observations: string;
  competitors: string[];
  accountingAccountCustomer: string;
  accountingAccountDeposit: string;
}


