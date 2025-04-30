import { AssoServiceDocument } from "./AssoServiceDocument";
import { AssoServiceFieldType } from "./AssoServiceFieldType";
import { IAttachment } from "./IAttachment";
import { ServiceType } from "./ServiceType";

export interface Service extends IAttachment {
  id: number;
  serviceTypes: ServiceType[];
  assoServiceDocuments: AssoServiceDocument[];
  assoServiceFieldTypes: AssoServiceFieldType[];
  hasMissingInformations: boolean;
  serviceStatus: string;
  servicePrice: number;
  confrereLabel: string;
  lastMissingAttachmentQueryDateTime: Date;
  serviceLabelToDisplay: string;
}
