import { Employee } from "../../profile/model/Employee";

export interface Kpi {
  id: number;
  applicationDate: Date;
  minValue: number;
  mediumValue: number;
  maxValue: number;
  baseValue: number;
  employee: Employee;
}
