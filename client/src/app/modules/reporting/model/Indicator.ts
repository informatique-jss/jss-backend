import { IReferential } from "../../administration/model/IReferential";
import { IndicatorGroup } from "./IndicatorGroup";

export interface Indicator extends IReferential {
  sqlText: string;
  lastUpdate: Date;
  indicatorGroup: IndicatorGroup;
}
