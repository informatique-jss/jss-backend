import { IReferential } from "../../main/model/IReferential";

export interface ReportingWidget extends IReferential {
  lastUpdate: Date;
  labelType: string;
  currentEvolution: number;
  lastValue: number;
  lastValueUnit: string;
  minHeight: number;
}
