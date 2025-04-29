import { IReferential } from "../../administration/model/IReferential";
import { IWorkflowElement } from "../../miscellaneous/model/IWorkflowElement";
import { SimpleProvision } from "./SimpleProvision";

export interface SimpleProvisionStatus extends IReferential, IWorkflowElement<SimpleProvision> {
}
