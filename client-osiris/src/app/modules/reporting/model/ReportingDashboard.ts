import { IReferential } from "../../main/model/IReferential";
import { ActiveDirectoryGroup } from "../../profile/model/ActiveDirectoryGroup";
import { AssoReportingDashboardWidget } from "./AssoReportingDashboardWidget";

export interface ReportingDashboard extends IReferential {
  dashboardOrder: number;
  activeDirectoryGroups: ActiveDirectoryGroup[];
  assoReportingDashboardWidgets: AssoReportingDashboardWidget[];
}
