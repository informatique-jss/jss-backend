import { Employee } from "../../profile/model/Employee";
import { Responsable } from "../../profile/model/Responsable";
import { CustomerOrder } from "./CustomerOrder";

export interface CustomerOrderComment {
  id: number;
  createdDateTime: Date;
  employee: Employee;
  currentCustomer: Responsable;
  comment: string;
  customerOrder: CustomerOrder;
  isFromTchat: boolean;
  isReadByCustomer: boolean;
}
