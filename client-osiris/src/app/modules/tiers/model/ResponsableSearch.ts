import { Employee } from "../../profile/model/Employee";
import { TiersCategory } from "../../profile/model/TiersCategory";

export interface ResponsableSearch {
  salesEmployee: Employee;
  mail: string;
  label: string;
  kpis: any;
  startDateKpis: Date;
  endDateKpis: Date;
  tiersCategory: TiersCategory;
}
