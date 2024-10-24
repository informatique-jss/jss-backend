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
  isRequiringNewUnregisteredAffaire: boolean;
  hasAnnouncement: boolean;
}
