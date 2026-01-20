import { Employee } from "../../profile/model/Employee";

export interface TiersSearch {
  salesEmployee: Employee;
  mail: string;
  label: string;
  isNewTiers: boolean;
  kpis: any;
  startDateKpis: Date;
  endDateKpis: Date;
  tiersCategory: string;
}
