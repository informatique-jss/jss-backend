import { AssoServiceProvisionType } from "./AssoServiceProvisionType";
import { AssoServiceTypeDocument } from "./AssoServiceTypeDocument";
import { AssoServiceTypeFieldType } from "./AssoServiceTypeFieldType";

export interface ServiceType {
  id: number;
  code: string;
  label: string;
  customLabel: string;
  comment: string;
  assoServiceTypeDocuments: AssoServiceTypeDocument[];
  assoServiceTypeFieldTypes: AssoServiceTypeFieldType[];
  assoServiceProvisionTypes: AssoServiceProvisionType[];
  isRequiringNewUnregisteredAffaire: boolean;
  isRequiringNewRegisteredAffaire: boolean;
  hasAnnouncement: boolean;
  hasOnlyAnnouncement: boolean;
  serviceTypeLinked: ServiceType;
  totalPreTaxPrice: number;
  deboursAmount: number;
  descriptionText: string;
}
