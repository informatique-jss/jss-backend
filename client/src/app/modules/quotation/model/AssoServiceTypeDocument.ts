import { AttachmentType } from "../../miscellaneous/model/AttachmentType";
import { ServiceType } from "./ServiceType";

export interface AssoServiceTypeDocument {
  id: number;
  service: ServiceType;
  attachmentType: AttachmentType;
  isMandatory: boolean;
}
