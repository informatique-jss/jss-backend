import { AttachmentType } from "./AttachmentType";
import { UploadedFile } from './UploadedFile';

export interface Attachment {
  id: number;
  description: string;
  attachmentType: AttachmentType;
  attachmentTypeAttachment: AttachmentType;
  creatDateTime: Date;
  attachmentDate: Date;
  uploadedFile: UploadedFile
}
