import { IReferential } from '../../../../administration/model/IReferential';
import { AttachmentType } from '../../../../miscellaneous/model/AttachmentType';
import { IAttachment } from '../../../../miscellaneous/model/IAttachment';
export interface TypeDocument extends IReferential, IAttachment {
  isToDownloadOnProvision: boolean;
  attachmentType: AttachmentType;
  customLabel: string;
}
