import { CustomerOrder } from './CustomerOrder';
import { IQuotation } from "./IQuotation";
import { QuotationStatus } from "./QuotationStatus";

export interface Quotation extends IQuotation {
  quotationStatus: QuotationStatus;
  customerOrders: CustomerOrder[];
}

