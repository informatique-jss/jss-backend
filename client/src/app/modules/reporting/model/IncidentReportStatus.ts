import { IReferential } from "../../administration/model/IReferential";
import { IWorkflowElement } from "../../miscellaneous/model/IWorkflowElement";
import { IncidentReport } from "./IncidentReport";

export interface IncidentReportStatus extends IReferential, IWorkflowElement<IncidentReport> {
}
