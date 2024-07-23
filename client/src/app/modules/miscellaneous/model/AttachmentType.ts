import { IReferential } from "../../administration/model/IReferential";
import { Attachment } from "./Attachment";
import { IAttachment } from "./IAttachment";
import { UploadedFile } from "./UploadedFile";

export interface AttachmentType extends IReferential, IAttachment {
  description: string;
  isToSentOnUpload: boolean;
  isToSentOnFinalizationMail: boolean;
  isHiddenFromUser: boolean;
}
