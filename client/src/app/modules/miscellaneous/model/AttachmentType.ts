import { IReferential } from "../../administration/model/IReferential";

export interface AttachmentType extends IReferential {
  description: string;
  isToSentOnUpload: boolean;
  isToSentOnFinalizationMail: boolean;
}
