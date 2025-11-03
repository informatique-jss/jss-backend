import { IWorkflowElement } from "../../miscellaneous/model/IWorkflowElement";
import { CustomerOrder } from "./CustomerOrder";

export interface OrderBlockage extends IWorkflowElement<CustomerOrder> {
  id: number;
  code: string;
  label: string;
}
