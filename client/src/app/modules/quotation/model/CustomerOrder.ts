import { AccountingRecord } from "../../accounting/model/AccountingRecord";
import { Deposit } from "../../invoicing/model/Deposit";
import { Payment } from "../../invoicing/model/Payment";
import { CustomerOrderStatus } from './CustomerOrderStatus';
import { Invoice } from "./Invoice";
import { IQuotation } from "./IQuotation";
import { Quotation } from "./Quotation";

export interface CustomerOrder extends IQuotation {
  quotations: Quotation[];
  deposits: Deposit[];
  invoices: Invoice[];
  payments: Payment[];
  accountingRecords: AccountingRecord[];
  customerOrderStatus: CustomerOrderStatus;
  centralPayPendingPaymentAmount: number;
  providerInvoices: Invoice[];
}
