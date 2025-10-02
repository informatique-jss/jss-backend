import { IReferential } from "../../administration/model/IReferential";
import { ActiveDirectoryGroup } from "../../miscellaneous/model/ActiveDirectoryGroup";
import { AssoReportingDashboardWidget } from "./AssoReportingDashboardWidget";

export interface ReportingDashboard extends IReferential {
  dashboardOrder: number;
  activeDirectoryGroups: ActiveDirectoryGroup[];
  assoReportingDashboardWidgets: AssoReportingDashboardWidget[];
}
