import { AccountingRecord } from "../../accounting/model/AccountingRecord";
import { PaymentType } from '../../miscellaneous/model/PaymentType';
import { AssoAffaireOrder } from "../../quotation/model/AssoAffaireOrder";
import { BankTransfert } from "../../quotation/model/BankTransfert";
import { CustomerOrder } from "../../quotation/model/CustomerOrder";
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
  commentPayment: string;
  checkNumber: string;
  originPayment: Payment;
  childrenPayments: Payment[];
  refund: Refund;
  provision: Provision;
  bankTransfert: BankTransfert;
  assoAffaireOrder: AssoAffaireOrder;
}
