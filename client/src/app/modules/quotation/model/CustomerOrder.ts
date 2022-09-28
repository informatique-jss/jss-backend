import { Deposit } from "../../invoicing/model/Deposit";
import { IQuotation } from "./IQuotation";
import { Quotation } from "./Quotation";

export interface CustomerOrder extends IQuotation {
  quotations: Quotation[];
  deposits: Deposit[];
}
