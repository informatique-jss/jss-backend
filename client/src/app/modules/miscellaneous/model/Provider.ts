import { AccountingAccount } from "../../accounting/model/AccountingAccount";
import { IReferential } from "../../administration/model/IReferential";
import { BillingItem } from './BillingItem';
import { IAttachment } from './IAttachment';
import { Mail } from "./Mail";
import { PaymentType } from "./PaymentType";
import { Phone } from "./Phone";
import { VatCollectionType } from "./VatCollectionType";

export interface Provider extends IReferential, IAttachment {
  id: number;
  label: string;
  accountingAccountCustomer: AccountingAccount;
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
}
