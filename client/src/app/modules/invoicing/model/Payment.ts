import { AccountingRecord } from "../../accounting/model/AccountingRecord";
import { PaymentType } from '../../miscellaneous/model/PaymentType';
import { CustomerOrder } from "../../quotation/model/CustomerOrder";
import { Invoice } from "../../quotation/model/Invoice";

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
  checkNumber: string;
}
