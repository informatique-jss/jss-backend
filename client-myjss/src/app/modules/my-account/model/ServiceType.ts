import { AssoServiceTypeDocument } from "./AssoServiceTypeDocument";
import { AssoServiceTypeFieldType } from "./AssoServiceTypeFieldType";

export interface ServiceType {
  id: number;
  code: string;
  label: string;
  comment: string;
  assoServiceTypeDocuments: AssoServiceTypeDocument[];
  assoServiceTypeFieldTypes: AssoServiceTypeFieldType[];
}
