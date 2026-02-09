import { Employee } from "../../profile/model/Employee";
import { Responsable } from "../../profile/model/Responsable";
import { CustomerOrder } from "./CustomerOrder";
import { Quotation } from "./Quotation";

export interface CustomerOrderComment {
  id: number;
  createdDateTime: Date;
  employee: Employee;
  currentCustomer: Responsable;
  comment: string;
  customerOrder: CustomerOrder;
  quotation: Quotation;
  isFromChat: boolean;
  isReadByCustomer: boolean;
  iquotationId: number;
}
