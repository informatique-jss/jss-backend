import { IReferential } from "../../administration/model/IReferential";

export interface Vat extends IReferential {
  rate: number;
  total: number; // just used in frontend to store total VAT for an IQuotation
}
