import { AccountingAccount } from "../../accounting/model/AccountingAccount";
import { City } from "../../miscellaneous/model/City";
import { Country } from "../../miscellaneous/model/Country";
import { Department } from "../../miscellaneous/model/Department";
import { Mail } from "../../miscellaneous/model/Mail";
import { Phone } from "../../miscellaneous/model/Phone";
import { WeekDay } from "../../miscellaneous/model/WeekDay";
import { JournalType } from "./JournalType";
import { Regie } from "./Regie";

export interface Confrere {
  id: number;
  label: string;
  code: string;
  mails: Mail[];
  phones: Phone[];
  accountingAccountCustomer: AccountingAccount;
  mailRecipient: string;
  address: string;
  city: City;
  postalCode: string;
  country: Country;
  iban: string;
  journalType: JournalType[];
  reinvoicing: number;
  weekDays: WeekDay[];
  discountRate: number;
  departments: Department[];
  lastShipmentForPublication: string;
  preference: string;
  shippingCosts: number;
  administrativeFees: number;
  numberOfPrint: number;
  accountingAccountProvider: AccountingAccount;
  regie: Regie;
}
