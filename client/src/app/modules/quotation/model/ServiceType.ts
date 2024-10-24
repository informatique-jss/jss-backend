import { AssoServiceProvisionType } from "./AssoServiceProvisionType";
import { AssoServiceTypeDocument } from "./AssoServiceTypeDocument";
import { AssoServiceTypeFieldType } from "./AssoServiceTypeFieldType";
import { ServiceFamily } from "./ServiceFamily";

export interface ServiceType {
  id: number;
  code: string;
  comment: string;
  label: string;
  customLabel: string;
  serviceFamily: ServiceFamily;
  assoServiceProvisionTypes: AssoServiceProvisionType[];
  assoServiceTypeDocuments: AssoServiceTypeDocument[];
  assoServiceTypeFieldTypes: AssoServiceTypeFieldType[];
  isRequiringNewUnregisteredAffaire: boolean;
  defaultDeboursPrice: number;
  defaultDeboursPriceNonTaxable: number;
}
