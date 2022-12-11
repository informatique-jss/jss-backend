import { AccountingAccount } from "../../accounting/model/AccountingAccount";
import { City } from "../../miscellaneous/model/City";
import { Country } from "../../miscellaneous/model/Country";
import { IAttachment } from "../../miscellaneous/model/IAttachment";
import { IDocument } from "../../miscellaneous/model/IDocument";
import { Language } from "../../miscellaneous/model/Language";
import { Mail } from "../../miscellaneous/model/Mail";
import { Phone } from "../../miscellaneous/model/Phone";
import { TiersFollowup } from "../../miscellaneous/model/TiersFollowup";
import { Employee } from "../../profile/model/Employee";

export interface ITiers extends IAttachment, IDocument {
  id: number;
  salesEmployee: Employee | undefined;
  formalisteEmployee: Employee | undefined;
  insertionEmployee: Employee | undefined;
  mailRecipient: string | null;
  language: Language;
  address: string;
  postalCode: string;
  cedexComplement: string;
  city: City;
  country: Country;
  mails: Mail[];
  phones: Phone[];
  tiersFollowups: TiersFollowup[];
  observations: string;
  accountingAccountCustomer: AccountingAccount;
  accountingAccountProvider: AccountingAccount;
  accountingAccountDeposit: AccountingAccount;
}
