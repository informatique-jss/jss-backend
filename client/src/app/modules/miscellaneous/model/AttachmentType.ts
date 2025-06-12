import { IReferential } from "../../administration/model/IReferential";
import { IAttachment } from "./IAttachment";

export interface AttachmentType extends IReferential, IAttachment {
  description: string;
  isToSentOnUpload: boolean;
  isToSentOnFinalizationMail: boolean;
  isHiddenFromUser: boolean;
  isDocumentDateRequired: boolean;
}
