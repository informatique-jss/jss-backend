import { AssoServiceDocument } from "./AssoServiceDocument";
import { AssoServiceFieldType } from "./AssoServiceFieldType";
import { IAttachment } from "./IAttachment";
import { ServiceType } from "./ServiceType";

export interface Service extends IAttachment {
  id: number;
  serviceType: ServiceType;
  assoServiceDocuments: AssoServiceDocument[];
  customLabel: string;
  customerComment: string;
  assoServiceFieldTypes: AssoServiceFieldType[];
  hasMissingInformations: boolean;
  serviceStatus: string;
  servicePrice: number;
}
