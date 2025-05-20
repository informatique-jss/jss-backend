import { AssoServiceDocument } from "./AssoServiceDocument";
import { AssoServiceFieldType } from "./AssoServiceFieldType";
import { IAttachment } from "./IAttachment";
import { Provision } from "./Provision";
import { ServiceType } from "./ServiceType";

export interface Service extends IAttachment {
  id: number;
  serviceTypes: ServiceType[];
  assoServiceDocuments: AssoServiceDocument[];
  assoServiceFieldTypes: AssoServiceFieldType[];
  hasMissingInformations: boolean;
  serviceStatus: string;
  serviceTotalPrice: number;
  servicePreTaxPrice: number;
  serviceVatPrice: number;
  serviceDiscountAmount: number;
  confrereLabel: string;
  lastMissingAttachmentQueryDateTime: Date;
  provisions: Provision[];
  serviceLabelToDisplay: string;
}
