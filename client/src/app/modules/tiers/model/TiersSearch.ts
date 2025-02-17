import { IndexEntity } from "src/app/routing/search/IndexEntity";
import { Employee } from "../../profile/model/Employee";

export interface TiersSearch {
  tiers: IndexEntity;
  withNonNullTurnover: boolean;
  responsable: IndexEntity;
  salesEmployee: Employee;
  mail: string;
  startDate: Date;
  endDate: Date;
  label: string;
  isNewTiers: boolean;
}
