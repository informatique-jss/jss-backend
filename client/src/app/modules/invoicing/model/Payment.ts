import { AccountingRecord } from "../../accounting/model/AccountingRecord";
import { PaymentType } from '../../miscellaneous/model/PaymentType';
import { CustomerOrder } from "../../quotation/model/CustomerOrder";
import { Invoice } from "../../quotation/model/Invoice";
import { PaymentWay } from "./PaymentWay";

export interface Payment {
  id: number;
  bankId: string;
  paymentDate: Date;
  paymentAmount: number;
  paymentWay: PaymentWay;
  label: string;
  invoice: Invoice;
  accountingRecords: AccountingRecord[];
  customerOrder: CustomerOrder;
  isExternallyAssociated: boolean;
  isCancelled: boolean;
  paymentType: PaymentType;
  commentPayment: string;
}
