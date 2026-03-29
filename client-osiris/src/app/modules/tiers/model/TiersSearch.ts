import { Employee } from "../../profile/model/Employee";
import { TiersCategory } from "../../profile/model/TiersCategory";
import { TiersGroup } from "../../profile/model/TiersGroup";

export interface TiersSearch {
  salesEmployee: Employee;
  mail: string;
  label: string;
  isNewTiers: boolean;
  kpis: any;
  startDateKpis: Date;
  endDateKpis: Date;
  tiersCategory: TiersCategory;
  tiersGroup: TiersGroup;
}
