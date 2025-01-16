import { IndexEntity } from "src/app/routing/search/IndexEntity";
import { Mail } from "../../miscellaneous/model/Mail";
import { Employee } from "../../profile/model/Employee";

export interface TiersSearch {
  tiers: IndexEntity;
  withNonNullTurnover: boolean;
  responsable: IndexEntity;
  salesEmployee: Employee;
  mail: Mail;
  startDate: Date;
  endDate: Date;
  label: string;
  isNewTiers: boolean;
}
