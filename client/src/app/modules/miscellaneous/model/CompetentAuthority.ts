import { AccountingAccount } from "../../accounting/model/AccountingAccount";
import { IReferential } from "../../administration/model/IReferential";
import { City } from "./City";
import { CompetentAuthorityType } from "./CompetentAuthorityType";
import { Country } from "./Country";
import { Department } from "./Department";
import { Mail } from "./Mail";
import { Phone } from "./Phone";
import { Region } from "./Region";

export interface CompetentAuthority extends IReferential {
  competentAuthorityType: CompetentAuthorityType;
  departments: Department[];
  phones: Phone[];
  mails: Mail[];
  iban: string;
  jssAccount: string;
  cities: City[];
  regions: Region[];
  hasAccount: boolean;
  contact: string;
  mailRecipient: string;
  address: string;
  city: City;
  postalCode: string;
  country: Country;
  accountingAccountProvider: AccountingAccount;
  accountingAccountCustomer: AccountingAccount;
}
