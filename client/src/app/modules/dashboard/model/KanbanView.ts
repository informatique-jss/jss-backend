import { IWorkflowElement } from "../../miscellaneous/model/IWorkflowElement";
import { Employee } from "../../profile/model/Employee";
import { SwimlaneType } from "./SwimlaneType";


export interface KanbanView<T, U extends IWorkflowElement<T>> {
  label: string;
  employees: Employee[];
  swimlaneType: SwimlaneType<T>;
  status: U[];
}


