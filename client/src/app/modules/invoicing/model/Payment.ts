import { AccountingRecord } from "../../accounting/model/AccountingRecord";
import { CustomerOrder } from "../../quotation/model/CustomerOrder";
import { Invoice } from "../../quotation/model/Invoice";
import { PaymentWay } from "./PaymentWay";

export interface Payment {
  id: number;
  paymentDate: Date;
  paymentAmount: number;
  paymentWay: PaymentWay;
  label: string;
  invoice: Invoice;
  accountingRecords: AccountingRecord[];
  customerOrder: CustomerOrder;
}
