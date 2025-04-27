import { AssoAffaireOrder } from "./AssoAffaireOrder";
import { AssoServiceDocument } from "./AssoServiceDocument";
import { AssoServiceFieldType } from './AssoServiceFieldType';
import { MissingAttachmentQuery } from "./MissingAttachmentQuery";
import { Provision } from "./Provision";
import { ServiceType } from "./ServiceType";

export interface Service {
  id: number;
  provisions: Provision[];
  assoAffaireOrder: AssoAffaireOrder;
  serviceType: ServiceType;
  assoServiceDocuments: AssoServiceDocument[];
  customLabel: string;
  customerComment: string;
  missingAttachmentQueries: MissingAttachmentQuery[];
  assoServiceFieldTypes: AssoServiceFieldType[];
  hasMissingInformations: boolean;
  serviceStatus: string;
  servicePrice: number;
  lastMissingAttachmentQueryDateTime: Date;
}
