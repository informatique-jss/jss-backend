import { AttachmentType } from 'src/app/modules/miscellaneous/model/AttachmentType';
import { IAttachment } from 'src/app/modules/miscellaneous/model/IAttachment';
import { IReferential } from '../../../../administration/model/IReferential';
export interface TypeDocument extends IReferential, IAttachment {
  isToDownloadOnProvision: boolean;
  attachmentType: AttachmentType;
  customLabel: string;
}
