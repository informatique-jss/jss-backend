import { IReferential } from "../../administration/model/IReferential";
import { IndicatorGroup } from "./IndicatorGroup";
import { Kpi } from "./Kpi";

export interface Indicator extends IReferential {
  sqlText: string;
  lastUpdate: Date;
  indicatorGroup: IndicatorGroup;
  kpis: Kpi[];
  isReverse: boolean;
}
