import { IReferential } from "../../administration/model/IReferential";
import { Vat } from "../../miscellaneous/model/Vat";
import { AccountingAccountClass } from "./AccountingAccountClass";

export interface AccountingAccount extends IReferential {
  accountingAccountNumber: string;
  vat: Vat;
  accountingAccountClass: AccountingAccountClass;
}
