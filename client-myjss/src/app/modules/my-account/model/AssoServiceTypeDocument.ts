import { AttachmentType } from "./AttachmentType";

export interface AssoServiceTypeDocument {
  id: number;
  attachmentType: AttachmentType;
  isMandatory: boolean;
}
