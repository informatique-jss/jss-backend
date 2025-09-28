import { IReferential } from "../../main/model/IReferential";

export interface ReportingWidget extends IReferential {
  lastUpdate: Date;
  labelType: string;
}
