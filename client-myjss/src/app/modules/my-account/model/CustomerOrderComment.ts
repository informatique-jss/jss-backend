import { Employee } from "../../profile/model/Employee";
import { Responsable } from "../../profile/model/Responsable";

export interface CustomerOrderComment {
  id: number;
  createdDateTime: Date;
  employee: Employee;
  currentCustomer: Responsable;
  comment: string;
}
