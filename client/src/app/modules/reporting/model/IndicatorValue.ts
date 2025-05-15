import { Employee } from "../../profile/model/Employee";

export interface IndicatorValue {
  id: number;
  employee: Employee;
  date: Date;
  value: number

  isMinValueReached: boolean;
  isMediumValueReached: boolean;
  isMaxValueReached: boolean;
  succededValue: number;
  succededPercentage: number;
}
