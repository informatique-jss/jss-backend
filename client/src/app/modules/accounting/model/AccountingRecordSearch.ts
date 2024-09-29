import { AccountingAccount } from "./AccountingAccount";
import { AccountingAccountClass } from "./AccountingAccountClass";
import { AccountingJournal } from "./AccountingJournal";

export interface AccountingRecordSearch {
  accountingClass: AccountingAccountClass;
  accountingAccount: AccountingAccount | undefined;
  accountingJournal: AccountingJournal | undefined;
  startDate: Date | undefined;
  endDate: Date | undefined;
  hideLettered: boolean;
  tiersId: number;
  idPayment: number;
  idCustomerOrder: number;
  idInvoice: number;
  idRefund: number;
  idBankTransfert: number;
  isFromAs400: boolean;
  isManual: boolean;
}
