import { IWorkflowElement } from "../../miscellaneous/model/IWorkflowElement";
import { CustomerOrder } from "../../quotation/model/CustomerOrder";

export interface InvoicingBlockage extends IWorkflowElement<CustomerOrder> {
  id: number;
  code: string;
  label: string;
}
