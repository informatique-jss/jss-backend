import { KpiCrm } from "./KpiCrm";

export interface KpiWidgetDto {
  kpiValue: number;
  kpiEvolution: number;
  kpiCrm: KpiCrm;
  labelType: string;
}
