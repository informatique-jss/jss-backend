import { AttachmentType } from "./AttachmentType";
import { IAttachment } from "./IAttachment";

export interface TypeDocument extends IAttachment {
  label: string;
  code: string;
  customLabel: string;
  attachmentType: AttachmentType;
}
