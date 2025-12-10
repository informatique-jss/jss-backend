import { Employee } from "../../profile/model/Employee";

export interface ResponsableSearch {
  salesEmployee: Employee;
  mail: string;
  label: string;
  kpis: any;
  startDateKpis: Date;
  endDateKpis: Date;
}
