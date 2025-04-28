import { AssoAffaireOrder } from "./AssoAffaireOrder";
import { AssoServiceDocument } from "./AssoServiceDocument";
import { AssoServiceFieldType } from './AssoServiceFieldType';
import { AssoServiceServiceType } from "./AssoServiceServiceType";
import { MissingAttachmentQuery } from "./MissingAttachmentQuery";
import { Provision } from "./Provision";

export interface Service {
  id: number;
  provisions: Provision[];
  assoAffaireOrder: AssoAffaireOrder;
  assoServiceServiceTypes: AssoServiceServiceType[];
  assoServiceDocuments: AssoServiceDocument[];
  customLabel: string;
  serviceLabelToDisplay: string;
  customerComment: string;
  missingAttachmentQueries: MissingAttachmentQuery[];
  assoServiceFieldTypes: AssoServiceFieldType[];
}
