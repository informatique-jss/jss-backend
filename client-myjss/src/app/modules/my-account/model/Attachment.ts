import { AttachmentType } from "./AttachmentType";
import { TypeDocument } from "./TypeDocument";

export interface Attachment {
  id: number;
  description: string;
  attachmentType: AttachmentType;
  typeDocument: TypeDocument;
  creatDateTime: Date;
}

