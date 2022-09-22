import { AccountingAccount } from "../../accounting/model/AccountingAccount";
import { IReferential } from "../../administration/model/IReferential";
import { City } from "../../miscellaneous/model/City";
import { Country } from "../../miscellaneous/model/Country";
import { Mail } from "../../miscellaneous/model/Mail";
import { Phone } from "../../miscellaneous/model/Phone";

export interface Regie extends IReferential {
  accountingAccountProvider: AccountingAccount;
  accountingAccountCustomer: AccountingAccount;
  accountingAccountDeposit: AccountingAccount;
  mailRecipient: string;
  address: string;
  city: City;
  postalCode: string;
  country: Country;
  phones: Phone[];
  mails: Mail[];
  iban: string;
}
