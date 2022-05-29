import { UploadedFile } from './../../miscellaneous/model/UploadedFile';
import { AttachmentType } from "./AttachmentType";
import { Responsable } from './Responsable';
import { Tiers } from "./Tiers";

export interface TiersAttachment {
  id: number;
  tiers: Tiers;
  responsable: Responsable;
  attachmentType: AttachmentType;
  uploadedFile: UploadedFile;
}

