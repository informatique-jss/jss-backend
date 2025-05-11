import { Employee } from "../../profile/model/Employee";

export interface IndicatorValue {
  id: number;
  employee: Employee;
  date: Date;
  value: number
}
