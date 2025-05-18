import { Employee } from "../../profile/model/Employee";
import { CustomerOrder } from "../../quotation/model/CustomerOrder";
import { Provision } from "../../quotation/model/Provision";
import { IncidentReportStatus } from "./IncidentReportStatus";
import { IncidentResponsibility } from "./IncidentResponsibility";

export interface IncidentReport {
  id: number;
  incidentReportStatus: IncidentReportStatus;
  incidentResponsibility: IncidentResponsibility;
  assignedTo: Employee;
  initiatedBy: Employee;
  startDate: Date;
  endDate: Date;
  title: string;
  description: string;
  customerImpact: string;
  jssImpact: string;
  detectionDescription: string;
  internalCommunicationDescription: string;
  externalCommunicationDescription: string;
  analysis: string;
  remedialActions: string;
  preventiveActionsProposal: string;
  managementComments: string;
  preventiveActions: string;
  costEstimation: number;
  provision: Provision;
  customerOrder: CustomerOrder;
}



