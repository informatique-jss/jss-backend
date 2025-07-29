import { Employee } from "../../profile/model/Employee";
import { AssignationType } from "./AssignationType";

export interface CustomerOrderAssignation {
  id: number;
  assignationType: AssignationType;
  employee: Employee;
  isAssigned: boolean;
}
