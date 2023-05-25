import { Department } from "./Department";
import { Vat } from "./Vat";

export interface DepartmentVatSetting {
  id: number;
  code: string;
  label: string;
  department: Department;
  intermediateVat: Vat;
  reducedVat: Vat;
  intermediateVatForPurshase: Vat;
  reducedVatForPurshase: Vat;
}
