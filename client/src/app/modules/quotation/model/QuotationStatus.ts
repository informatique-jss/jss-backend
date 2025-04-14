import { IReferential } from "../../administration/model/IReferential";
import { IWorkflowElement } from "../../miscellaneous/model/IWorkflowElement";
import { Quotation } from "./Quotation";

export interface QuotationStatus extends IReferential, IWorkflowElement<Quotation> {

}
