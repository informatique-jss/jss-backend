import { Employee } from "../../profile/model/Employee";

export interface KpiCrmSearchModel {
  endDateKpis?: Date;
  startDateKpis?: Date;
  salesEmployee?: Employee;
  salesEmployeeId?: number;
  tiersIds: number[];
  responsableIds: number[];
  isAllTiers: boolean;
  kpiScale: string;
}
