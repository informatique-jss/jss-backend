import { ReportingWidget } from './ReportingWidget';
export interface AssoReportingDashboardWidget {
  id: number;
  reportingWidget: ReportingWidget;
  widgetOrder: number;
  classToUse: string;
}
