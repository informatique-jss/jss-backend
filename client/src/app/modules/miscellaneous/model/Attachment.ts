import { AttachmentType } from "./AttachmentType";
import { UploadedFile } from "./UploadedFile";


export interface Attachment {
  id: number;
  attachmentType: AttachmentType;
  uploadedFile: UploadedFile;
  isDisabled: boolean;
}

