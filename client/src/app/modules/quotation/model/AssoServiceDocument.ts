import { AttachmentType } from '../../miscellaneous/model/AttachmentType';
import { IAttachment } from '../../miscellaneous/model/IAttachment';
import { Service } from "./Service";

export interface AssoServiceDocument extends IAttachment {
  service: Service;
  attachmentType: AttachmentType;
  isMandatory: boolean;
  formalisteComment: string;
}
