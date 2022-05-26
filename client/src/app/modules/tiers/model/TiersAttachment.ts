import { UploadedFile } from './../../miscellaneous/model/UploadedFile';
import { AttachmentType } from "./AttachmentType";
import { Tiers } from "./Tiers";

export interface TiersAttachment {
  id: number;
  tiers: Tiers;
  attachmentType: AttachmentType;
  uploadedFile: UploadedFile;
}

