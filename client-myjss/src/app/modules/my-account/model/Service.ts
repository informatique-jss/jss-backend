import { AssoServiceDocument } from "./AssoServiceDocument";
import { AssoServiceFieldType } from "./AssoServiceFieldType";
import { ServiceType } from "./ServiceType";

export interface Service {
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
