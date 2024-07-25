import { AccountingAccount } from "../../accounting/model/AccountingAccount";
import { IReferential } from "../../administration/model/IReferential";
import { Vat } from "./Vat";

export interface BillingType extends IReferential {
  canOverridePrice: boolean;
  isPriceBasedOnCharacterNumber: boolean;
  isOverrideVat: boolean;
  isNonTaxable: boolean;
  isDebour: boolean;
  isFee: boolean;
  vat: Vat;
  isOptionnal: boolean;
  accountingAccountProduct: AccountingAccount;
  accountingAccountCharge: AccountingAccount;
  isGenerateAccountProduct: boolean;
  isGenerateAccountCharge: boolean;
  isUsedForInsertionRff: boolean;
  isUsedForFormaliteRff: boolean;
  isVacation: boolean;
  isTraitement: boolean;
}
