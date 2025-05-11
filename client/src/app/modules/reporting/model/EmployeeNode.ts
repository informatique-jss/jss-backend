import { Employee } from "../../profile/model/Employee";

export interface EmployeeNode {
  name: string;
  children?: EmployeeNode[];
  employee?: Employee;
  parent?: EmployeeNode;
}
