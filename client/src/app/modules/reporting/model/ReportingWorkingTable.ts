import { IReferential } from "../../administration/model/IReferential";

export interface ReportingWorkingTable extends IReferential {
  sqlText: string;
  reportingUpdateFrequency: string;
  viewName: string;
  isToPersist: boolean;
}
