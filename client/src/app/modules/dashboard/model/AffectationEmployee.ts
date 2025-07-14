import { IWorkflowElement } from "../../miscellaneous/model/IWorkflowElement";
import { Employee } from "../../profile/model/Employee";

export interface AffectationEmployee<T> extends Employee, IWorkflowElement<T> {
  id: number;
}
