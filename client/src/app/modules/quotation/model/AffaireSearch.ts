import { IWorkflowElement } from '../../miscellaneous/model/IWorkflowElement';
import { Employee } from "../../profile/model/Employee";

export interface AffaireSearch {
  responsible: Employee | undefined;
  assignedTo: Employee | undefined;
  label: string | undefined;
  status: IWorkflowElement[];
}
