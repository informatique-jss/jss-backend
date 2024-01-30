import { IndexEntity } from "src/app/routing/search/IndexEntity";
import { Employee } from "../../profile/model/Employee";

export interface RffSearch {
  tiers: IndexEntity;
  responsable: IndexEntity;
  salesEmployee: Employee;
  startDate: Date;
  endDate: Date;
  isHideCancelledRff: boolean;
  withNonNullTurnover: boolean;
  label: string;
}
