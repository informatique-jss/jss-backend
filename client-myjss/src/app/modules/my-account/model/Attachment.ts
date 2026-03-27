import { AttachmentType } from "./AttachmentType";
import { TypeDocument } from "./TypeDocument";
import { UploadedFile } from './UploadedFile';

export interface Attachment {
  id: number;
  description: string;
  attachmentType: AttachmentType;
  typeDocument: TypeDocument;
  creatDateTime: Date;
  attachmentDate: Date;
  uploadedFile: UploadedFile
}