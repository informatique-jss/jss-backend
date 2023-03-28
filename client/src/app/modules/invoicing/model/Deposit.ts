import { AccountingRecord } from "../../accounting/model/AccountingRecord";
import { CustomerOrder } from "../../quotation/model/CustomerOrder";
import { Invoice } from "../../quotation/model/Invoice";
import { Payment } from "./Payment";

export interface Deposit {
  id: number;
  label: string;
  depositDate: Date;
  depositAmount: number;
  accountingRecords: AccountingRecord[];
  invoice: Invoice;
  customerOrder: CustomerOrder;
  originPayment: Payment;
}
