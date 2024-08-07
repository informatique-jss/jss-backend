import { AssoAffaireOrder } from "./AssoAffaireOrder";
import { AssoServiceDocument } from "./AssoServiceDocument";
import { MissingAttachmentQuery } from "./MissingAttachmentQuery";
import { Provision } from "./Provision";
import { ServiceType } from "./ServiceType";
import { AssoServiceFieldType } from './AssoServiceFieldType';

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
}
