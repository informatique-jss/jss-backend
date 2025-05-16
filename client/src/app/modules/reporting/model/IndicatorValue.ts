import { Employee } from "../../profile/model/Employee";
import { Indicator } from "./Indicator";

export interface IndicatorValue {
  id: number;
  employee: Employee;
  date: Date;
  value: number
  indicator: Indicator;
  isMinValueReached: boolean;
  isMediumValueReached: boolean;
  isMaxValueReached: boolean;
  succededValue: number;
  succededPercentage: number;
}
