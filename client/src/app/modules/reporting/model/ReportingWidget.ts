import { IReferential } from "../../administration/model/IReferential";
import { ReportingWidgetSerie } from "./ReportingWidgetSerie";

export interface ReportingWidget extends IReferential {
  reportingUpdateFrequency: string;
  reportingWidgetSeries: ReportingWidgetSerie[];
  label: string;
  labelType: string;
  lastValueUnit: string;
  minHeight: number;
}
