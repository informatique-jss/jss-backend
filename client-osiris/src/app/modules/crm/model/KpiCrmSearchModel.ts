import { Employee } from "../../profile/model/Employee";

export interface KpiCrmSearchModel {
  [key: string]: any;
  endDateKpis?: Date;
  startDateKpis?: Date;
  salesEmployee?: Employee;
  salesEmployeeId?: number;
  tiersIds: number[];
  responsableIds: number[];
  isAllTiers: boolean;
  kpiScale: string;
}
