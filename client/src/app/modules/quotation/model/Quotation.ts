import { IQuotation } from "./IQuotation";
import { CustomerOrder } from "./CustomerOrder";

export interface Quotation extends IQuotation {
  customerOrder: CustomerOrder;
}
