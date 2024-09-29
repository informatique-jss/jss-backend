import { AssoServiceTypeFieldType } from "./AssoServiceTypeFieldType";
import { AssoServiceProvisionType } from "./AssoServiceProvisionType";
import { AssoServiceTypeDocument } from "./AssoServiceTypeDocument";
import { ServiceFamily } from "./ServiceFamily";

export interface ServiceType {
  id: number;
  code: string;
  label: string;
  serviceFamily: ServiceFamily;
  assoServiceProvisionTypes: AssoServiceProvisionType[];
  assoServiceTypeDocuments: AssoServiceTypeDocument[];
  assoServiceTypeFieldTypes: AssoServiceTypeFieldType[];
}
