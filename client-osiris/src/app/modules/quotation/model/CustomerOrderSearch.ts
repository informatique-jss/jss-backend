import { Employee } from "../../profile/model/Employee";

export interface CustomerOrderSearch {
  salesEmployee: Employee;
  mail: string;
  startDate: Date;
  endDate: Date;
  responsables: number[];
}
