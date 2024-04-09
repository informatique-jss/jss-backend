import { AccountingAccount } from "../../accounting/model/AccountingAccount";
import { IReferential } from "../../administration/model/IReferential";
import { AssoMailCompetentAuthorityServiceFamilyGroup } from "./AssoMailCompetentAuthorityServiceFamilyGroup";
import { City } from "./City";
import { CompetentAuthorityType } from "./CompetentAuthorityType";
import { Country } from "./Country";
import { Department } from "./Department";
import { IAttachment } from './IAttachment';
import { Mail } from "./Mail";
import { PaymentType } from './PaymentType';
import { Phone } from "./Phone";
import { Region } from "./Region";

export interface CompetentAuthority extends IReferential, IAttachment {
  competentAuthorityType: CompetentAuthorityType;
  departments: Department[];
  phones: Phone[];
  mails: Mail[];
  accountingMails: Mail[];
  iban: string;
  bic: string;
  cities: City[];
  regions: Region[];
  contact: string;
  mailRecipient: string;
  address: string;
  city: City;
  postalCode: string;
  cedexComplement: string;
  country: Country;
  accountingAccountProvider: AccountingAccount;
  accountingAccountCustomer: AccountingAccount;
  accountingAccountDepositProvider: AccountingAccount
  reinvoicing: number;
  schedulle: string;
  observations: string;
  paymentTypes: PaymentType[];
  defaultPaymentType: PaymentType;
  inpiReference: string;
  azureCustomReference: string;
  intercommunityVat: string;
  isNotToReminder: boolean;
  assoMailCompetentAuthorityServiceFamilyGroups: AssoMailCompetentAuthorityServiceFamilyGroup[];
}
