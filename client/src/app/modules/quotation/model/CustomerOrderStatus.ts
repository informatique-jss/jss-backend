import { IReferential } from "../../administration/model/IReferential";
import { IWorkflowElement } from "../../miscellaneous/model/IWorkflowElement";
import { CustomerOrder } from "./CustomerOrder";

export interface CustomerOrderStatus extends IReferential, IWorkflowElement<CustomerOrder> {
}
