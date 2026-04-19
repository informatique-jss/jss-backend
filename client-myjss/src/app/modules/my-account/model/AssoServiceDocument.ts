import { AttachmentType } from "./AttachmentType";
import { IAttachment } from "./IAttachment";

export interface AssoServiceDocument extends IAttachment {
  id: number;
  attachmentType: AttachmentType;
  isMandatory: boolean;
  formalisteComment: string;
}
