import { AccountingAccount } from "../../accounting/model/AccountingAccount";
import { AccountingRecord } from "../../accounting/model/AccountingRecord";
import { CompetentAuthority } from "../../miscellaneous/model/CompetentAuthority";
import { PaymentType } from '../../miscellaneous/model/PaymentType';
import { Provider } from "../../miscellaneous/model/Provider";
import { AssoAffaireOrder } from "../../quotation/model/AssoAffaireOrder";
import { BankTransfert } from "../../quotation/model/BankTransfert";
import { CustomerOrder } from "../../quotation/model/CustomerOrder";
import { DirectDebitTransfert } from "../../quotation/model/DirectDebitTransfert";
import { Invoice } from "../../quotation/model/Invoice";
import { Provision } from "../../quotation/model/Provision";
import { Refund } from "./Refund";

export interface Payment {
  id: number;
  bankId: string;
  paymentDate: Date;
  paymentAmount: number;
  label: string;
  invoice: Invoice;
  accountingRecords: AccountingRecord[];
  customerOrder: CustomerOrder;
  isExternallyAssociated: boolean;
  isCancelled: boolean;
  isAppoint: boolean;
  isDeposit: boolean;
  paymentType: PaymentType;
  comment: string;
  checkNumber: string;
  checkDepositNumber: string;
  originPayment: Payment;
  childrenPayments: Payment[];
  refund: Refund;
  provision: Provision;
  bankTransfert: BankTransfert;
  assoAffaireOrder: AssoAffaireOrder;
  directDebitTransfert: DirectDebitTransfert;
  competentAuthority: CompetentAuthority;
  provider: Provider;
  accountingAccount: AccountingAccount;
  matchAutomation: string;
  matchType: string;
}
