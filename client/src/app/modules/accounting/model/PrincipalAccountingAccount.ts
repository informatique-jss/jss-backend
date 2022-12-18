import { IReferential } from '../../administration/model/IReferential';
import { AccountingAccountClass } from './AccountingAccountClass';
export interface PrincipalAccountingAccount extends IReferential {
  accountingAccountClass: AccountingAccountClass;
}
