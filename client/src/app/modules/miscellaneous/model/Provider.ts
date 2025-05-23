import { AccountingAccount } from "../../accounting/model/AccountingAccount";
import { IReferential } from "../../administration/model/IReferential";
import { BillingItem } from './BillingItem';
import { City } from "./City";
import { Country } from "./Country";
import { IAttachment } from './IAttachment';
import { Mail } from "./Mail";
import { PaymentType } from "./PaymentType";
import { Phone } from "./Phone";
import { TiersFollowup } from "./TiersFollowup";
import { VatCollectionType } from "./VatCollectionType";

export interface Provider extends IReferential, IAttachment {
  id: number;
  label: string;
  accountingAccountProvider: AccountingAccount;
  accountingAccountDeposit: AccountingAccount;
  defaultBillingItem: BillingItem;
  iban: string;
  bic: string;
  accountingMails: Mail[];
  mails: Mail[];
  phones: Phone[];
  jssReference: string;
  vatCollectionType: VatCollectionType;
  paymentType: PaymentType;
  country: Country;
  postalCode: string;
  cedexComplement: string;
  address: string;
  siren: string;
  siret: string;
  city: City;
  idAs400: number;
  isRemindProviderInvoice: boolean;
  tiersFollowups: TiersFollowup[];
}
