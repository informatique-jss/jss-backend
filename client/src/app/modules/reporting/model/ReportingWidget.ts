import { IReferential } from "../../administration/model/IReferential";
import { ReportingWidgetSerie } from "./ReportingWidgetSerie";

export interface ReportingWidget extends IReferential {
  reportingUpdateFrequency: string;
  reportingWidgetSeries: ReportingWidgetSerie[];
  labelSqlText: string;
  label: string;
  labelType: string;
}
