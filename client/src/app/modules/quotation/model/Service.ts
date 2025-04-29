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
  serviceTypes: ServiceType[];
  assoServiceDocuments: AssoServiceDocument[];
  customLabel: string;
  serviceLabelToDisplay: string;
  customerComment: string;
  missingAttachmentQueries: MissingAttachmentQuery[];
  assoServiceFieldTypes: AssoServiceFieldType[];
}
