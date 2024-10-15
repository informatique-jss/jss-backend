import { AttachmentType } from "./AttachmentType";

export interface TypeDocument {
  label: string;
  code: string;
  customLabel: string;
  attachmentType: AttachmentType;
}
